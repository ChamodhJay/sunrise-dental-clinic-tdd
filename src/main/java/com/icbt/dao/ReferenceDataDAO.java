package com.icbt.dao;

import com.icbt.model.ClinicFeeSchedule;
import com.icbt.model.Dentist;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ReferenceDataDAO {
    private static final String DENTIST_COLUMNS = """
            d.dentist_id AS d_dentist_id, d.booking_enabled AS d_booking_enabled,
            u.user_id AS su_user_id, u.username AS su_username,
            u.password_hash AS su_password_hash, u.full_name AS su_full_name,
            u.role AS su_role, u.active AS su_active, u.created_at AS su_created_at
            """;

    public List<Dentist> findActiveDentists() {
        String sql = "SELECT " + DENTIST_COLUMNS + " FROM dentist d JOIN staff_user u "
                + "ON u.user_id = d.staff_user_id WHERE d.booking_enabled = TRUE "
                + "AND u.active = TRUE AND u.role = 'DENTIST' ORDER BY u.full_name";
        List<Dentist> dentists = new ArrayList<>();
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                dentists.add(JdbcMapper.dentist(resultSet, "d_", "su_"));
            }
            return dentists;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load dentists", exception);
        }
    }

    public Optional<Dentist> findDentistById(UUID dentistId) {
        return findDentist("d.dentist_id = ?", dentistId);
    }

    public Optional<Dentist> findDentistByStaffUserId(UUID staffUserId) {
        return findDentist("d.staff_user_id = ?", staffUserId);
    }

    private Optional<Dentist> findDentist(String condition, UUID id) {
        String sql = "SELECT " + DENTIST_COLUMNS + " FROM dentist d JOIN staff_user u "
                + "ON u.user_id = d.staff_user_id WHERE " + condition;
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(JdbcMapper.dentist(resultSet, "d_", "su_"))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the dentist profile", exception);
        }
    }

    public List<TreatmentType> findTreatmentTypes(boolean activeOnly) {
        String sql = "SELECT treatment_type_id AS t_treatment_type_id, name AS t_treatment_name, "
                + "base_price AS t_base_price, active AS t_treatment_active FROM treatment_type "
                + (activeOnly ? "WHERE active = TRUE " : "") + "ORDER BY name";
        List<TreatmentType> treatments = new ArrayList<>();
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                treatments.add(JdbcMapper.treatmentType(resultSet, "t_"));
            }
            return treatments;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load treatment types", exception);
        }
    }

    public Optional<TreatmentType> findTreatmentTypeById(UUID treatmentTypeId) {
        String sql = "SELECT treatment_type_id AS t_treatment_type_id, name AS t_treatment_name, "
                + "base_price AS t_base_price, active AS t_treatment_active FROM treatment_type "
                + "WHERE treatment_type_id = ?";
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, treatmentTypeId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(JdbcMapper.treatmentType(resultSet, "t_"))
                        : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the treatment type", exception);
        }
    }

    public Optional<ClinicFeeSchedule> findActiveFeeSchedule() {
        String sql = "SELECT fee_schedule_id, consultation_fee, effective_from, active "
                + "FROM clinic_fee_schedule WHERE active = TRUE ORDER BY effective_from DESC LIMIT 1";
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? Optional.of(mapFee(resultSet)) : Optional.empty();
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the consultation fee", exception);
        }
    }

    public void saveTreatment(UUID treatmentTypeId, String name, BigDecimal price,
                              boolean active, StaffUser manager) {
        String call = "{CALL sp_save_treatment(?, ?, ?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, treatmentTypeId.toString());
            statement.setString(2, name);
            statement.setBigDecimal(3, price);
            statement.setBoolean(4, active);
            statement.setString(5, manager.getUserId().toString());
            statement.execute();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 31301) {
                throw new SecurityException(
                        "Only an active clinic manager may maintain treatments", exception);
            }
            throw new DataAccessException("Could not save the treatment type", exception);
        }
    }

    public ClinicFeeSchedule replaceActiveFee(BigDecimal fee, LocalDate effectiveFrom,
                                               StaffUser manager) {
        UUID id = UUID.randomUUID();
        String call = "{CALL sp_replace_active_fee(?, ?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, id.toString());
            statement.setBigDecimal(2, fee);
            statement.setDate(3, Date.valueOf(effectiveFrom));
            statement.setString(4, manager.getUserId().toString());
            statement.execute();
            return new ClinicFeeSchedule(id, fee, effectiveFrom, true);
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 31401) {
                throw new SecurityException("Only an active clinic manager may change fees", exception);
            }
            throw new DataAccessException("Could not update the consultation fee", exception);
        }
    }

    private ClinicFeeSchedule mapFee(ResultSet resultSet) throws SQLException {
        return new ClinicFeeSchedule(
                UUID.fromString(resultSet.getString("fee_schedule_id")),
                resultSet.getBigDecimal("consultation_fee"),
                resultSet.getDate("effective_from").toLocalDate(),
                resultSet.getBoolean("active"));
    }
}
