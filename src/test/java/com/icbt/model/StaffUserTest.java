package com.icbt.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class StaffUserTest {
    @Test
    public void sessionPrincipalDoesNotRetainPasswordHash() {
        StaffUser account = new StaffUser(UUID.randomUUID(), "manager", "sensitive-hash",
                "Clinic Manager", StaffRole.CLINIC_MANAGER, true,
                LocalDateTime.of(2026, 8, 20, 9, 0));

        StaffUser principal = account.asSessionPrincipal();

        assertNotSame(account, principal);
        assertEquals("", principal.getPasswordHash());
        assertEquals(account.getUserId(), principal.getUserId());
        assertEquals(account.getRole(), principal.getRole());
    }
}
