package com.icbt.service;

import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffUser;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public final class AuthenticationService {
    private static final int MAX_FAILURES = 3;
    private static final Duration LOCK_DURATION = Duration.ofSeconds(30);

    private final StaffUserDAO userDAO;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public AuthenticationService() {
        this(new StaffUserDAO(), new PasswordHasher(), Clock.systemUTC());
    }

    AuthenticationService(StaffUserDAO userDAO, PasswordHasher passwordHasher, Clock clock) {
        this.userDAO = userDAO;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public StaffUser authenticate(String username, char[] password, String clientAddress) {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        String key = normalized + "|" + (clientAddress == null ? "unknown" : clientAddress);
        Instant now = clock.instant();
        AttemptState current = attempts.get(key);
        if (current != null && current.lockedUntil != null && now.isBefore(current.lockedUntil)) {
            long seconds = Math.max(1, Duration.between(now, current.lockedUntil).toSeconds());
            throw new AuthenticationException("Too many failed attempts. Try again shortly.", seconds);
        }

        StaffUser user = normalized.isBlank() || normalized.length() > 50 || password.length > 128
                ? null : userDAO.findByUsername(normalized).orElse(null);
        boolean valid = user != null && user.isActive() && passwordHasher.verify(password, user.getPasswordHash());
        if (!valid) {
            AttemptState failed = attempts.compute(key, (ignored, state) -> {
                int count = state == null || state.lockedUntil != null ? 1 : state.failureCount + 1;
                Instant lockedUntil = count >= MAX_FAILURES ? now.plus(LOCK_DURATION) : null;
                return new AttemptState(count, lockedUntil);
            });
            if (failed.lockedUntil != null) {
                throw new AuthenticationException("Too many failed attempts. Try again in 30 seconds.", 30);
            }
            throw new AuthenticationException("Invalid username or password");
        }

        attempts.remove(key);
        return user;
    }

    private static final class AttemptState {
        private final int failureCount;
        private final Instant lockedUntil;

        private AttemptState(int failureCount, Instant lockedUntil) {
            this.failureCount = failureCount;
            this.lockedUntil = lockedUntil;
        }
    }
}
