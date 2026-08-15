package com.icbt.service;

import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class UserAccountService {
    private final StaffUserDAO staffUserDAO;
    private final PasswordHasher passwordHasher;
    private final UserAccountValidator validator;

    public UserAccountService() {
        this(new StaffUserDAO(), new PasswordHasher(), new UserAccountValidator());
    }

    UserAccountService(StaffUserDAO staffUserDAO, PasswordHasher passwordHasher,
                       UserAccountValidator validator) {
        this.staffUserDAO = staffUserDAO;
        this.passwordHasher = passwordHasher;
        this.validator = validator;
    }

    public List<StaffUser> findAll(StaffUser manager) {
        requireManager(manager);
        return staffUserDAO.findAll();
    }

    public StaffUser create(StaffUser manager, String username, String fullName,
                            char[] password, char[] confirmation, String roleValue) {
        requireManager(manager);
        StaffRole role = validator.validateNewAccount(
                username, fullName, password, confirmation, roleValue);
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        if (staffUserDAO.findByUsername(normalizedUsername).isPresent()) {
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("username", "That username is already in use.");
            throw new ValidationException(errors);
        }
        String passwordHash = passwordHasher.hash(password);
        return staffUserDAO.create(UUID.randomUUID(), UUID.randomUUID(), normalizedUsername,
                passwordHash, fullName.trim(), role.name(), manager);
    }

    public void resetPassword(StaffUser manager, String userId, char[] password,
                              char[] confirmation) {
        requireManager(manager);
        UUID targetId = parseUserId(userId);
        validator.validatePasswordReset(password, confirmation);
        requireExistingUser(targetId);
        staffUserDAO.resetPassword(targetId, passwordHasher.hash(password), manager);
    }

    public void setActive(StaffUser manager, String userId, boolean active) {
        requireManager(manager);
        UUID targetId = parseUserId(userId);
        if (!active && manager.getUserId().equals(targetId)) {
            throw new BusinessRuleException("You cannot deactivate your own account.");
        }
        requireExistingUser(targetId);
        staffUserDAO.setActive(targetId, active, manager);
    }

    private StaffUser requireExistingUser(UUID userId) {
        return staffUserDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("The selected user account was not found."));
    }

    private UUID parseUserId(String value) {
        try {
            return UUID.fromString(value == null ? "" : value.trim());
        } catch (IllegalArgumentException exception) {
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("userId", "The selected user identifier is invalid.");
            throw new ValidationException(errors);
        }
    }

    private void requireManager(StaffUser user) {
        if (user == null || !user.hasRole(StaffRole.CLINIC_MANAGER)) {
            throw new SecurityException("Only the clinic manager can manage user accounts.");
        }
    }
}
