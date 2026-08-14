package com.icbt.dao;

import com.icbt.model.StaffUser;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class StaffUserDAO {
    private static final String USER_COLUMNS = """
            SELECT user_id AS su_user_id, username AS su_username,
                   password_hash AS su_password_hash, full_name AS su_full_name,
                   role AS su_role, active AS su_active, created_at AS su_created_at
            FROM staff_user
            """;

    public Optional<StaffUser> findByUsername(String username) {
        return findOne(USER_COLUMNS + " WHERE LOWER(username) = LOWER(?)", username);
    }

    public Optional<StaffUser> findById(UUID userId) {
        return userId == null ? Optional.empty()
                : findOne(USER_COLUMNS + " WHERE user_id = ?", userId.toString());
    }

    public List<StaffUser> findAll() {
        String sql = USER_COLUMNS + " ORDER BY full_name, username";
        List<StaffUser> users = new ArrayList<>();
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(JdbcMapper.staffUser(resultSet, "su_"));
            }
            return users;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not list staff accounts", exception);
        }
    }

    public StaffUser create(UUID userId, UUID dentistId, String username, String passwordHash,
                            String fullName, String role, StaffUser manager) {
        String call = "{CALL sp_create_staff_user(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, userId.toString());
            statement.setString(2, username);
            statement.setString(3, passwordHash);
            statement.setString(4, fullName);
            statement.setString(5, role);
            statement.setString(6, manager.getUserId().toString());
            statement.setString(7, dentistId.toString());
            statement.execute();
            return findById(userId)
                    .orElseThrow(() -> new DataAccessException(
                            "Created staff account could not be reloaded", null));
        } catch (SQLException exception) {
            throw accountWriteFailure("create the staff account", exception);
        }
    }

    public void resetPassword(UUID userId, String passwordHash, StaffUser manager) {
        String call = "{CALL sp_reset_staff_password(?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, userId.toString());
            statement.setString(2, passwordHash);
            statement.setString(3, manager.getUserId().toString());
            statement.execute();
        } catch (SQLException exception) {
            throw accountWriteFailure("reset the staff password", exception);
        }
    }

    public void setActive(UUID userId, boolean active, StaffUser manager) {
        String call = "{CALL sp_set_staff_active(?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, userId.toString());
            statement.setBoolean(2, active);
            statement.setString(3, manager.getUserId().toString());
            statement.execute();
        } catch (SQLException exception) {
            throw accountWriteFailure("change the staff account status", exception);
        }
    }

    private Optional<StaffUser> findOne(String sql, String value) {
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(JdbcMapper.staffUser(resultSet, "su_"))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not read the staff account", exception);
        }
    }

    private RuntimeException accountWriteFailure(String operation, SQLException exception) {
        return switch (exception.getErrorCode()) {
            case 31501, 31601, 31701 -> new SecurityException(
                    "Only an active clinic manager may manage staff accounts", exception);
            case 31504, 1062 -> new BusinessRuleException("That username is already in use.");
            case 31502 -> new BusinessRuleException("The submitted account details are invalid.");
            case 31503 -> new BusinessRuleException("Select a valid staff role.");
            case 31602, 31702 -> new NotFoundException("The selected user account was not found.");
            case 31603 -> new BusinessRuleException("The new password could not be stored securely.");
            case 31703 -> new BusinessRuleException("You cannot deactivate your own account.");
            case 31704 -> new BusinessRuleException("The last active clinic manager cannot be deactivated.");
            case 31705 -> new BusinessRuleException(
                    "This dentist still has scheduled appointments and cannot be deactivated.");
            default -> new DataAccessException("Could not " + operation, exception);
        };
    }
}
