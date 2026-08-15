package com.icbt.service;

import com.icbt.model.StaffRole;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserAccountValidatorTest {
    private final UserAccountValidator validator = new UserAccountValidator();

    @Test
    public void acceptsValidAccountDetails() {
        StaffRole role = validator.validateNewAccount("new.dentist", "Dr. Anjali Silva",
                "Secure@1234".toCharArray(), "Secure@1234".toCharArray(), "DENTIST");
        assertEquals(StaffRole.DENTIST, role);
    }

    @Test
    public void rejectsEmptyFieldsMismatchAndInvalidRole() {
        try {
            validator.validateNewAccount("", "", new char[0], "different".toCharArray(), "ADMIN");
            fail("Expected account validation to fail");
        } catch (ValidationException exception) {
            assertTrue(exception.getFieldErrors().containsKey("username"));
            assertTrue(exception.getFieldErrors().containsKey("fullName"));
            assertTrue(exception.getFieldErrors().containsKey("password"));
            assertTrue(exception.getFieldErrors().containsKey("confirmPassword"));
            assertTrue(exception.getFieldErrors().containsKey("role"));
        }
    }

    @Test
    public void rejectsWeakOrMismatchedResetPassword() {
        try {
            validator.validatePasswordReset("short".toCharArray(), "other".toCharArray());
            fail("Expected password reset validation to fail");
        } catch (ValidationException exception) {
            assertTrue(exception.getFieldErrors().containsKey("password"));
            assertTrue(exception.getFieldErrors().containsKey("confirmPassword"));
        }
    }
}
