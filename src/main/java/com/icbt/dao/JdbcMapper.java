package com.icbt.dao;

import com.icbt.model.Dentist;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

final class JdbcMapper {
    private JdbcMapper() { }

    static StaffUser staffUser(ResultSet resultSet, String prefix) throws SQLException {
        return new StaffUser(
                UUID.fromString(resultSet.getString(prefix + "user_id")),
                resultSet.getString(prefix + "username"),
                resultSet.getString(prefix + "password_hash"),
                resultSet.getString(prefix + "full_name"),
                StaffRole.valueOf(resultSet.getString(prefix + "role")),
                resultSet.getBoolean(prefix + "active"),
                resultSet.getTimestamp(prefix + "created_at").toLocalDateTime());
    }

    static Dentist dentist(ResultSet resultSet, String dentistPrefix, String staffPrefix) throws SQLException {
        return new Dentist(
                UUID.fromString(resultSet.getString(dentistPrefix + "dentist_id")),
                staffUser(resultSet, staffPrefix),
                resultSet.getBoolean(dentistPrefix + "booking_enabled"));
    }

    static TreatmentType treatmentType(ResultSet resultSet, String prefix) throws SQLException {
        return new TreatmentType(
                UUID.fromString(resultSet.getString(prefix + "treatment_type_id")),
                resultSet.getString(prefix + "treatment_name"),
                resultSet.getBigDecimal(prefix + "base_price"),
                resultSet.getBoolean(prefix + "treatment_active"));
    }
}
