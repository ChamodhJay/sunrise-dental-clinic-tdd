package com.icbt.service;

import com.icbt.model.StaffRole;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class UserAccountValidator {
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{3,50}");
    private static final Pattern FULL_NAME = Pattern.compile("[\\p{L} .'-]{2,100}");

    public StaffRole validateNewAccount(String username, String fullName, char[] password,
                                        char[] confirmation, String roleValue) {
        Map<String, String> errors = new LinkedHashMap<>();
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedName = fullName == null ? "" : fullName.trim();
        if (!USERNAME.matcher(normalizedUsername).matches()) {
            errors.put("username",
                    "Username must contain 3-50 letters, numbers, dots, underscores, or hyphens.");
        }
        if (!FULL_NAME.matcher(normalizedName).matches()) {
            errors.put("fullName", "Enter a valid full name containing 2-100 characters.");
        }
        validatePassword(password, confirmation, errors);
        StaffRole role = parseRole(roleValue, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return role;
    }

    public void validatePasswordReset(char[] password, char[] confirmation) {
        Map<String, String> errors = new LinkedHashMap<>();
        validatePassword(password, confirmation, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private StaffRole parseRole(String roleValue, Map<String, String> errors) {
        try {
            return StaffRole.valueOf(roleValue == null ? "" : roleValue.trim());
        } catch (IllegalArgumentException exception) {
            errors.put("role", "Select Receptionist, Dentist, or Clinic Manager.");
            return null;
        }
    }

    private void validatePassword(char[] password, char[] confirmation,
                                  Map<String, String> errors) {
        char[] supplied = password == null ? new char[0] : password;
        char[] repeated = confirmation == null ? new char[0] : confirmation;
        if (supplied.length < 10 || supplied.length > 128
                || !containsUppercase(supplied) || !containsLowercase(supplied)
                || !containsDigit(supplied) || !containsSymbol(supplied)) {
            errors.put("password",
                    "Password must be 10-128 characters and include uppercase, lowercase, number, and symbol.");
        }
        if (!Arrays.equals(supplied, repeated)) {
            errors.put("confirmPassword", "Password confirmation does not match.");
        }
    }

    private boolean containsUppercase(char[] value) {
        return anyMatch(value, Character::isUpperCase);
    }

    private boolean containsLowercase(char[] value) {
        return anyMatch(value, Character::isLowerCase);
    }

    private boolean containsDigit(char[] value) {
        return anyMatch(value, Character::isDigit);
    }

    private boolean containsSymbol(char[] value) {
        return anyMatch(value, character -> !Character.isLetterOrDigit(character));
    }

    private boolean anyMatch(char[] value, CharacterRule rule) {
        for (char character : value) {
            if (rule.matches(character)) {
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface CharacterRule {
        boolean matches(char character);
    }
}
