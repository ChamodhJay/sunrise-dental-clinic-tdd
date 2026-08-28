package com.icbt.dao;

import com.icbt.model.Appointment;
import com.icbt.model.AppointmentStatus;
import com.icbt.model.Dentist;
import com.icbt.model.Patient;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentRecord;
import com.icbt.model.TreatmentType;
import com.icbt.service.RegisterAppointmentCommand;
import com.icbt.service.SchedulingConflictException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AppointmentDAO {
    private static final String APPOINTMENT_SELECT = """
            SELECT a.appointment_id, a.appointment_number, a.appointment_date,
                   a.appointment_time, a.status AS appointment_status, a.created_at AS appointment_created_at,
                   p.patient_id, p.full_name AS patient_name, p.contact_number, p.address,
                   p.created_at AS patient_created_at,
                   d.dentist_id AS d_dentist_id, d.booking_enabled AS d_booking_enabled,
                   du.user_id AS du_user_id, du.username AS du_username,
                   '' AS du_password_hash, du.full_name AS du_full_name,
                   du.role AS du_role, du.active AS du_active, du.created_at AS du_created_at,
                   t.treatment_type_id AS t_treatment_type_id, t.name AS t_treatment_name,
                   t.base_price AS t_base_price, t.active AS t_treatment_active,
                   ru.user_id AS ru_user_id, ru.username AS ru_username,
                   '' AS ru_password_hash, ru.full_name AS ru_full_name,
                   ru.role AS ru_role, ru.active AS ru_active, ru.created_at AS ru_created_at,
                   tr.treatment_record_id, tr.diagnosis, tr.treatment_notes,
                   tr.completed_at, tr.created_at AS treatment_created_at
            FROM appointment a
            JOIN patient p ON p.patient_id = a.patient_id
            JOIN dentist d ON d.dentist_id = a.dentist_id
            JOIN staff_user du ON du.user_id = d.staff_user_id
            JOIN treatment_type t ON t.treatment_type_id = a.treatment_type_id
            JOIN staff_user ru ON ru.user_id = a.registered_by
            LEFT JOIN treatment_record tr ON tr.appointment_id = a.appointment_id
            """;

    public Appointment create(RegisterAppointmentCommand command, Dentist dentist,
                              TreatmentType treatmentType, StaffUser registeredBy) {
        UUID candidatePatientId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        String call = StoredProgramDefinition.REGISTER_APPOINTMENT;
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, candidatePatientId.toString());
            statement.setString(2, appointmentId.toString());
            statement.setString(3, command.patientName().trim());
            statement.setString(4, command.address().trim());
            statement.setString(5, normalizePhone(command.contactNumber()));
            statement.setString(6, dentist.getDentistId().toString());
            statement.setString(7, treatmentType.getTreatmentTypeId().toString());
            statement.setString(8, registeredBy.getUserId().toString());
            statement.setDate(9, Date.valueOf(command.appointmentDate()));
            statement.setTime(10, Time.valueOf(command.appointmentTime()));
            statement.registerOutParameter(11, Types.VARCHAR);
            statement.registerOutParameter(12, Types.CHAR);
            statement.registerOutParameter(13, Types.TIMESTAMP);
            statement.registerOutParameter(14, Types.TIMESTAMP);
            statement.execute();

            String appointmentNumber = statement.getString(11);
            UUID patientId = UUID.fromString(statement.getString(12));
            Patient patient = new Patient(patientId, command.patientName().trim(),
                    normalizePhone(command.contactNumber()), command.address().trim(),
                    statement.getTimestamp(13).toLocalDateTime());
            return new Appointment(appointmentId, appointmentNumber, patient, dentist, treatmentType,
                    registeredBy, command.appointmentDate(), command.appointmentTime(),
                    AppointmentStatus.SCHEDULED,
                    statement.getTimestamp(14).toLocalDateTime(), null);
        } catch (SQLException exception) {
            if (isDuplicateKey(exception)) {
                throw new SchedulingConflictException(
                        "That dentist already has an appointment at the selected date and time.", exception);
            }
            throw new DataAccessException("Could not register the appointment", exception);
        }
    }

    public boolean existsScheduledSlot(UUID dentistId, LocalDate date, LocalTime time) {
        String sql = "SELECT 1 FROM appointment WHERE dentist_id = ? AND appointment_date = ? "
                + "AND appointment_time = ? AND status = 'SCHEDULED' LIMIT 1";
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dentistId.toString());
            statement.setDate(2, Date.valueOf(date));
            statement.setTime(3, Time.valueOf(time));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not check dentist availability", exception);
        }
    }

    public Optional<Appointment> findByNumber(String appointmentNumber) {
        return findOne(APPOINTMENT_SELECT + " WHERE UPPER(a.appointment_number) = UPPER(?)", appointmentNumber);
    }

    public Optional<Appointment> findById(UUID appointmentId) {
        return findOne(APPOINTMENT_SELECT + " WHERE a.appointment_id = ?", appointmentId.toString());
    }

    public List<Appointment> findAssignedToDentist(UUID dentistId) {
        String sql = APPOINTMENT_SELECT + " WHERE a.dentist_id = ? "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        return findMany(sql, dentistId.toString(), null);
    }

    public List<Appointment> findForDate(LocalDate date) {
        String sql = APPOINTMENT_SELECT + " WHERE a.appointment_date = ? "
                + "ORDER BY a.appointment_time, du.full_name";
        return findMany(sql, null, date);
    }

    public int countForDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM appointment WHERE appointment_date = ?";
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not count today's appointments", exception);
        }
    }

    public int countCompletedThisMonth(LocalDate monthOf) {
        String sql = """
                SELECT COUNT(*) FROM treatment_record tr
                JOIN appointment a ON a.appointment_id = tr.appointment_id
                WHERE YEAR(tr.completed_at) = ? AND MONTH(tr.completed_at) = ?
                """;
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, monthOf.getYear());
            statement.setInt(2, monthOf.getMonthValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not count completed visits", exception);
        }
    }

    public void recordTreatment(UUID appointmentId, UUID dentistId, String diagnosis, String notes) {
        String call = StoredProgramDefinition.RECORD_TREATMENT;
        try (Connection connection = DBConnectionFactory.getConnection();
             CallableStatement statement = connection.prepareCall(call)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, appointmentId.toString());
            statement.setString(3, dentistId.toString());
            statement.setString(4, diagnosis.trim());
            statement.setString(5, notes.trim());
            statement.execute();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 31102) {
                throw new SecurityException("Only the assigned dentist may record this treatment", exception);
            }
            if (exception.getErrorCode() == 31101) {
                throw new IllegalStateException("Appointment no longer exists", exception);
            }
            if (exception.getErrorCode() == 31103 || exception.getErrorCode() == 31105
                    || isDuplicateKey(exception)) {
                throw new IllegalStateException("Treatment has already been recorded", exception);
            }
            throw new DataAccessException("Could not record treatment details", exception);
        }
    }

    private Optional<Appointment> findOne(String sql, String value) {
        List<Appointment> values = findMany(sql, value, null);
        return values.stream().findFirst();
    }

    private List<Appointment> findMany(String sql, String textValue, LocalDate dateValue) {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = DBConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (textValue != null) {
                statement.setString(1, textValue);
            } else if (dateValue != null) {
                statement.setDate(1, Date.valueOf(dateValue));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    appointments.add(mapAppointment(resultSet));
                }
            }
            return appointments;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load appointment data", exception);
        }
    }

    private Appointment mapAppointment(ResultSet resultSet) throws SQLException {
        StaffUser dentistUser = JdbcMapper.staffUser(resultSet, "du_");
        Dentist dentist = new Dentist(UUID.fromString(resultSet.getString("d_dentist_id")),
                dentistUser, resultSet.getBoolean("d_booking_enabled"));
        Patient patient = new Patient(
                UUID.fromString(resultSet.getString("patient_id")),
                resultSet.getString("patient_name"),
                resultSet.getString("contact_number"),
                resultSet.getString("address"),
                resultSet.getTimestamp("patient_created_at").toLocalDateTime());
        TreatmentType treatment = JdbcMapper.treatmentType(resultSet, "t_");
        StaffUser registeredBy = JdbcMapper.staffUser(resultSet, "ru_");
        String treatmentRecordId = resultSet.getString("treatment_record_id");
        TreatmentRecord record = treatmentRecordId == null ? null : new TreatmentRecord(
                UUID.fromString(treatmentRecordId), dentist,
                resultSet.getString("diagnosis"), resultSet.getString("treatment_notes"),
                resultSet.getTimestamp("completed_at").toLocalDateTime(),
                resultSet.getTimestamp("treatment_created_at").toLocalDateTime());
        return new Appointment(
                UUID.fromString(resultSet.getString("appointment_id")),
                resultSet.getString("appointment_number"), patient, dentist, treatment, registeredBy,
                resultSet.getDate("appointment_date").toLocalDate(),
                resultSet.getTime("appointment_time").toLocalTime(),
                AppointmentStatus.valueOf(resultSet.getString("appointment_status")),
                resultSet.getTimestamp("appointment_created_at").toLocalDateTime(), record);
    }

    private boolean isDuplicateKey(SQLException exception) {
        SQLException current = exception;
        while (current != null) {
            if (current.getErrorCode() == 1062) {
                return true;
            }
            current = current.getNextException();
        }
        return false;
    }

    private String normalizePhone(String phone) {
        return phone.replace(" ", "").trim();
    }
}
