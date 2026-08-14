package com.icbt.service;

import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {
    private static final String CLIENT_ADDRESS = "127.0.0.1";

    private StaffUserDAO userDAO;
    private PasswordHasher passwordHasher;
    private MutableClock clock;
    private AuthenticationService authenticationService;
    private StaffUser activeUser;

    @Before
    public void setUp() {
        userDAO = mock(StaffUserDAO.class);
        passwordHasher = mock(PasswordHasher.class);
        clock = new MutableClock(Instant.parse("2026-08-14T08:00:00Z"), ZoneOffset.UTC);
        authenticationService = new AuthenticationService(userDAO, passwordHasher, clock);
        activeUser = staffUser(true);
    }

    @Test
    public void validCredentialsNormalizeUsernameAndReturnActiveUser() {
        when(userDAO.findByUsername("reception")).thenReturn(Optional.of(activeUser));
        when(passwordHasher.verify(any(char[].class), eq("stored-hash"))).thenReturn(true);

        StaffUser authenticated = authenticationService.authenticate(
                "  ReCePtIoN  ", "correct-password".toCharArray(), CLIENT_ADDRESS);

        assertSame(activeUser, authenticated);
        verify(userDAO).findByUsername("reception");
        verify(passwordHasher).verify(any(char[].class), eq("stored-hash"));
    }

    @Test
    public void incorrectPasswordIsRejectedWithGenericMessage() {
        when(userDAO.findByUsername("reception")).thenReturn(Optional.of(activeUser));
        when(passwordHasher.verify(any(char[].class), eq("stored-hash"))).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate(
                        "reception", "wrong-password".toCharArray(), CLIENT_ADDRESS));

        assertEquals("Invalid username or password", exception.getMessage());
        assertEquals(0, exception.getRetryAfterSeconds());
    }

    @Test
    public void inactiveAccountIsRejectedWithoutCheckingPassword() {
        StaffUser inactiveUser = staffUser(false);
        when(userDAO.findByUsername("reception")).thenReturn(Optional.of(inactiveUser));

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate(
                        "reception", "correct-password".toCharArray(), CLIENT_ADDRESS));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(passwordHasher, never()).verify(any(char[].class), any(String.class));
    }

    @Test
    public void thirdFailedAttemptLocksTheUsernameAndClient() {
        when(userDAO.findByUsername("unknown")).thenReturn(Optional.empty());

        AuthenticationException first = failedAuthentication("unknown", CLIENT_ADDRESS);
        AuthenticationException second = failedAuthentication("unknown", CLIENT_ADDRESS);
        AuthenticationException third = failedAuthentication("unknown", CLIENT_ADDRESS);
        AuthenticationException stillLocked = failedAuthentication("unknown", CLIENT_ADDRESS);

        assertEquals("Invalid username or password", first.getMessage());
        assertEquals("Invalid username or password", second.getMessage());
        assertEquals(30, third.getRetryAfterSeconds());
        assertEquals(30, stillLocked.getRetryAfterSeconds());
        verify(userDAO, times(3)).findByUsername("unknown");
    }

    @Test
    public void validCredentialsWorkAfterTheLockExpires() {
        when(userDAO.findByUsername("reception"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(activeUser));
        when(passwordHasher.verify(any(char[].class), eq("stored-hash"))).thenReturn(true);

        failedAuthentication("reception", CLIENT_ADDRESS);
        failedAuthentication("reception", CLIENT_ADDRESS);
        AuthenticationException locked = failedAuthentication("reception", CLIENT_ADDRESS);
        assertEquals(30, locked.getRetryAfterSeconds());

        clock.advance(Duration.ofSeconds(31));

        StaffUser authenticated = authenticationService.authenticate(
                "reception", "correct-password".toCharArray(), CLIENT_ADDRESS);
        assertSame(activeUser, authenticated);
        verify(userDAO, times(4)).findByUsername("reception");
    }

    private AuthenticationException failedAuthentication(String username, String clientAddress) {
        return assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate(
                        username, "wrong-password".toCharArray(), clientAddress));
    }

    private StaffUser staffUser(boolean active) {
        return new StaffUser(
                UUID.fromString("5b38cbd5-9752-4b5a-8e55-a786a9cae8f0"),
                "reception",
                "stored-hash",
                "Receptionist",
                StaffRole.RECEPTIONIST,
                active,
                LocalDateTime.of(2026, 8, 1, 9, 0));
    }

    private static final class MutableClock extends Clock {
        private Instant currentInstant;
        private final ZoneId zone;

        private MutableClock(Instant currentInstant, ZoneId zone) {
            this.currentInstant = currentInstant;
            this.zone = zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId newZone) {
            return new MutableClock(currentInstant, newZone);
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }

        private void advance(Duration duration) {
            currentInstant = currentInstant.plus(duration);
        }
    }
}
