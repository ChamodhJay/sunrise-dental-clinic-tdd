package com.icbt.service;

import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffUser;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class AuthenticationService {
    private static final int MAX_FAILURES = 3;
    private static final Duration LOCK_DURATION = Duration.ofSeconds(30);
    private static final Duration ATTEMPT_RETENTION = Duration.ofMinutes(10);
    private static final int MAX_TRACKED_ATTEMPTS = 10_000;
    private static final int CLEANUP_INTERVAL = 256;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MAX_CLIENT_ADDRESS_LENGTH = 64;
    private static final String DUMMY_PASSWORD_HASH =
            "pbkdf2$120000$cmVjZXB0aW9uLXNlZWQwMQ==$"
                    + "lzoN9GEuNxyuqj9Lb0bLw8ag+zGzjuUnqtCBafem6KU=";

    private final StaffUserDAO userDAO;
    private final PasswordHasher passwordHasher;
    private final Clock clock;
    private final int maxTrackedAttempts;
    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final AtomicInteger authenticationCount = new AtomicInteger();

    public AuthenticationService() {
        this(new StaffUserDAO(), new PasswordHasher(), Clock.systemUTC());
    }

    AuthenticationService(StaffUserDAO userDAO, PasswordHasher passwordHasher, Clock clock) {
        this(userDAO, passwordHasher, clock, MAX_TRACKED_ATTEMPTS);
    }

    AuthenticationService(StaffUserDAO userDAO, PasswordHasher passwordHasher, Clock clock,
            int maxTrackedAttempts) {
        if (maxTrackedAttempts < 1) {
            throw new IllegalArgumentException("maxTrackedAttempts must be positive");
        }
        this.userDAO = userDAO;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
        this.maxTrackedAttempts = maxTrackedAttempts;
    }

    public StaffUser authenticate(String username, char[] password, String clientAddress) {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        char[] suppliedPassword = password == null ? new char[0] : password;
        String trackedUsername = normalized.length() <= MAX_USERNAME_LENGTH ? normalized : "<invalid>";
        String key = trackedUsername + "|" + normalizeClientAddress(clientAddress);
        Instant now = clock.instant();
        removeExpiredAttemptsPeriodically(now);
        AttemptState current = attempts.get(key);
        if (current != null && current.lockedUntil != null && now.isBefore(current.lockedUntil)) {
            long seconds = Math.max(1, Duration.between(now, current.lockedUntil).toSeconds());
            throw new AuthenticationException("Too many failed attempts. Try again shortly.", seconds);
        }

        boolean credentialsWithinLimits = !normalized.isBlank()
                && normalized.length() <= MAX_USERNAME_LENGTH
                && suppliedPassword.length <= MAX_PASSWORD_LENGTH;
        StaffUser user = !credentialsWithinLimits
                ? null : userDAO.findByUsername(normalized).orElse(null);
        String passwordHash = user == null ? DUMMY_PASSWORD_HASH : user.getPasswordHash();
        boolean passwordMatches = credentialsWithinLimits
                && passwordHasher.verify(suppliedPassword, passwordHash);
        boolean valid = user != null && user.isActive() && passwordMatches;
        if (!valid) {
            AttemptState failed = recordFailure(key, now);
            if (failed != null && failed.lockedUntil != null) {
                throw new AuthenticationException("Too many failed attempts. Try again in 30 seconds.", 30);
            }
            throw new AuthenticationException("Invalid username or password");
        }

        attempts.remove(key);
        return user;
    }

    private synchronized AttemptState recordFailure(String key, Instant now) {
        if (attempts.size() >= maxTrackedAttempts && !attempts.containsKey(key)) {
            var existingKey = attempts.keySet().iterator();
            if (existingKey.hasNext()) {
                attempts.remove(existingKey.next());
            }
        }
        return attempts.compute(key, (ignored, state) -> {
                int count = state == null || state.lockedUntil != null ? 1 : state.failureCount + 1;
                Instant lockedUntil = count >= MAX_FAILURES ? now.plus(LOCK_DURATION) : null;
                return new AttemptState(count, lockedUntil, now);
        });
    }

    private void removeExpiredAttemptsPeriodically(Instant now) {
        if (authenticationCount.incrementAndGet() % CLEANUP_INTERVAL != 0) {
            return;
        }
        attempts.entrySet().removeIf(entry ->
                !now.isBefore(entry.getValue().lastFailedAt.plus(ATTEMPT_RETENTION)));
    }

    private String normalizeClientAddress(String clientAddress) {
        String normalized = clientAddress == null || clientAddress.isBlank()
                ? "unknown" : clientAddress.trim();
        return normalized.length() <= MAX_CLIENT_ADDRESS_LENGTH
                ? normalized : normalized.substring(0, MAX_CLIENT_ADDRESS_LENGTH);
    }

    private static final class AttemptState {
        private final int failureCount;
        private final Instant lockedUntil;
        private final Instant lastFailedAt;

        private AttemptState(int failureCount, Instant lockedUntil, Instant lastFailedAt) {
            this.failureCount = failureCount;
            this.lockedUntil = lockedUntil;
            this.lastFailedAt = lastFailedAt;
        }
    }
}
