package com.icbt.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class StaffUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final String username;
    private final String passwordHash;
    private final String fullName;
    private final StaffRole role;
    private final boolean active;
    private final LocalDateTime createdAt;

    public StaffUser(UUID userId, String username, String passwordHash, String fullName,
                     StaffRole role, boolean active, LocalDateTime createdAt) {
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.fullName = Objects.requireNonNull(fullName);
        this.role = Objects.requireNonNull(role);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public StaffRole getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean hasRole(StaffRole requiredRole) { return active && role == requiredRole; }
}
