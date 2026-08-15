package com.icbt.service;

import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserAccountAuthorizationTest {
    private final UserAccountService service = new UserAccountService();

    @Test
    public void receptionistCannotListUserAccounts() {
        assertUnauthorized(() -> service.findAll(user(StaffRole.RECEPTIONIST)));
    }

    @Test
    public void dentistCannotModifyAnotherAccount() {
        assertUnauthorized(() -> service.setActive(
                user(StaffRole.DENTIST), UUID.randomUUID().toString(), false));
    }

    @Test
    public void invalidUserIdentifierIsRejectedBeforeDatabaseAccess() {
        try {
            service.resetPassword(user(StaffRole.CLINIC_MANAGER), "not-a-uuid",
                    "Secure@1234".toCharArray(), "Secure@1234".toCharArray());
            fail("Expected invalid user identifier to be rejected");
        } catch (ValidationException exception) {
            assertTrue(exception.getFieldErrors().containsKey("userId"));
        }
    }

    @Test
    public void managerCannotDeactivateOwnAccount() {
        StaffUser manager = user(StaffRole.CLINIC_MANAGER);
        try {
            service.setActive(manager, manager.getUserId().toString(), false);
            fail("Expected self-deactivation to be rejected");
        } catch (BusinessRuleException exception) {
            assertEquals("You cannot deactivate your own account.", exception.getMessage());
        }
    }

    private void assertUnauthorized(Runnable operation) {
        try {
            operation.run();
            fail("Expected manager-only operation to be rejected");
        } catch (SecurityException exception) {
            assertEquals("Only the clinic manager can manage user accounts.", exception.getMessage());
        }
    }

    private StaffUser user(StaffRole role) {
        return new StaffUser(UUID.randomUUID(), role.name().toLowerCase(), "not-exposed",
                "Test User", role, true, LocalDateTime.now());
    }
}
