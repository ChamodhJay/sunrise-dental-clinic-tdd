package com.icbt.dao;

import com.icbt.model.Appointment;
import com.icbt.model.Bill;
import com.icbt.model.BillLine;
import com.icbt.model.BillLineType;
import com.icbt.model.BillStatus;
import com.icbt.model.ClinicFeeSchedule;
import com.icbt.model.StaffUser;
import com.icbt.service.BusinessRuleException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BillDAO {
    private static final String BILL_SELECT = """
            SELECT b.bill_id, b.bill_number, b.total_amount, b.status AS bill_status, b.generated_at,
                   u.user_id AS gu_user_id, u.username AS gu_username,
                   u.password_hash AS gu_password_hash, u.full_name AS gu_full_name,
                   u.role AS gu_role, u.active AS gu_active, u.created_at AS gu_created_at,
                   f.fee_schedule_id, f.consultation_fee, f.effective_from, f.active AS fee_active,
                   l.bill_line_id, l.line_type, l.description, l.amount
            FROM bill b
            JOIN staff_user u ON u.user_id = b.generated_by
            JOIN clinic_fee_schedule f ON f.fee_schedule_id = b.fee_schedule_id
            JOIN bill_line l ON l.bill_id = b.bill_id
            """;

    public Optional<Bill> findByAppointment(Appointment appointment) {
        return find(BILL_SELECT + " WHERE b.appointment_id = ? ORDER BY l.line_type",
                appointment, appointment.getAppointmentId().toString());
    }

    public Optional<Bill> findByNumber(Appointment appointment, String billNumber) {
        return find(BILL_SELECT + " WHERE UPPER(b.bill_number) = UPPER(?) ORDER BY l.line_type",
                appointment, billNumber);
    }

    public Bill createForCompletedAppointment(Appointment appointment, StaffUser generatedBy) {
        String call = "{CALL sp_create_bill(?, ?, ?, ?)}";
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, appointment.getAppointmentId().toString());
            statement.setString(3, generatedBy.getUserId().toString());
            statement.registerOutParameter(4, Types.VARCHAR);
            statement.execute();
            String generatedNumber = statement.getString(4);
            return findByAppointment(appointment)
                    .orElseThrow(() -> new DataAccessException(
                            "Stored procedure returned bill " + generatedNumber + " but it could not be reloaded", null));
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 31201) {
                throw new SecurityException("Only an active receptionist may generate a bill", exception);
            }
            if (exception.getErrorCode() == 31202) {
                throw new BusinessRuleException(
                        "A bill can be generated only after the dentist records treatment.");
            }
            if (exception.getErrorCode() == 31203) {
                throw new BusinessRuleException("Exactly one active consultation fee must be configured.");
            }
            throw new DataAccessException("Could not save the patient bill", exception);
        }
    }

    public void updateStatus(UUID billId, BillStatus status) {
        String sql = "UPDATE bill SET status = ? WHERE bill_id = ?";
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setString(2, billId.toString());
            if (statement.executeUpdate() != 1) {
                throw new DataAccessException("Bill status update affected no rows", null);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not update bill print status", exception);
        }
    }

    private Optional<Bill> find(String sql, Appointment appointment, String value) {
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                UUID billId = UUID.fromString(resultSet.getString("bill_id"));
                String billNumber = resultSet.getString("bill_number");
                java.math.BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
                BillStatus status = BillStatus.valueOf(resultSet.getString("bill_status"));
                LocalDateTime generatedAt = resultSet.getTimestamp("generated_at").toLocalDateTime();
                StaffUser generatedBy = JdbcMapper.staffUser(resultSet, "gu_");
                ClinicFeeSchedule fee = new ClinicFeeSchedule(
                        UUID.fromString(resultSet.getString("fee_schedule_id")),
                        resultSet.getBigDecimal("consultation_fee"),
                        resultSet.getDate("effective_from").toLocalDate(),
                        resultSet.getBoolean("fee_active"));
                List<BillLine> lines = new ArrayList<>();
                do {
                    lines.add(new BillLine(
                            UUID.fromString(resultSet.getString("bill_line_id")),
                            BillLineType.valueOf(resultSet.getString("line_type")),
                            resultSet.getString("description"), resultSet.getBigDecimal("amount")));
                } while (resultSet.next());
                return Optional.of(new Bill(billId, billNumber, appointment, generatedBy, fee,
                        totalAmount, status, generatedAt, lines));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the patient bill", exception);
        }
    }

}
