package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.Appointment;
import com.icbt.model.Dentist;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class AppointmentService {
    private final AppointmentDAO appointmentDAO;
    private final ReferenceDataDAO referenceDataDAO;
    private final AppointmentValidator validator;
    private final Clock clock;

    public AppointmentService() {
        this(new AppointmentDAO(), new ReferenceDataDAO(), new AppointmentValidator(), Clock.systemDefaultZone());
    }

    AppointmentService(AppointmentDAO appointmentDAO, ReferenceDataDAO referenceDataDAO,
            AppointmentValidator validator, Clock clock) {
        this.appointmentDAO = appointmentDAO;
        this.referenceDataDAO = referenceDataDAO;
        this.validator = validator;
        this.clock = clock;
    }

    public Appointment register(RegisterAppointmentCommand command, StaffUser user) {
        requireRole(user, StaffRole.RECEPTIONIST);
        validator.validate(command, LocalDate.now(clock));
        Dentist dentist = referenceDataDAO.findDentistById(command.dentistId())
                .filter(Dentist::isActiveForBooking)
                .orElseThrow(() -> new BusinessRuleException("The selected dentist is unavailable for booking."));
        TreatmentType treatment = referenceDataDAO.findTreatmentTypeById(command.treatmentTypeId())
                .filter(TreatmentType::isActive)
                .orElseThrow(() -> new BusinessRuleException("The selected treatment type is inactive."));
        if (appointmentDAO.existsScheduledSlot(dentist.getDentistId(),
                command.appointmentDate(), command.appointmentTime())) {
            throw new SchedulingConflictException(
                    "That dentist is unavailable at the selected date and time. Choose another slot.");
        }
        return appointmentDAO.create(command, dentist, treatment, user);
    }

    public Appointment findByNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            throw new BusinessRuleException("Enter an appointment number.");
        }
        return appointmentDAO.findByNumber(appointmentNumber.trim())
                .orElseThrow(() -> new NotFoundException("Appointment not found."));
    }

    public List<Appointment> findAssignedAppointments(StaffUser dentistUser) {
        requireRole(dentistUser, StaffRole.DENTIST);
        Dentist dentist = referenceDataDAO.findDentistByStaffUserId(dentistUser.getUserId())
                .orElseThrow(() -> new BusinessRuleException("This user has no dentist profile."));
        return appointmentDAO.findAssignedToDentist(dentist.getDentistId());
    }

    public void recordTreatment(UUID appointmentId, StaffUser dentistUser,
            String diagnosis, String treatmentNotes) {
        requireRole(dentistUser, StaffRole.DENTIST);
        if (appointmentId == null) {
            throw new BusinessRuleException("Select a valid appointment.");
        }
        String normalizedDiagnosis = diagnosis == null ? "" : diagnosis.trim();
        String normalizedNotes = treatmentNotes == null ? "" : treatmentNotes.trim();
        if (normalizedDiagnosis.isEmpty() || normalizedDiagnosis.length() > 500
                || normalizedNotes.isEmpty() || normalizedNotes.length() > 2000) {
            throw new BusinessRuleException(
                    "Diagnosis and treatment notes are required and must fit the stated length limits.");
        }
        Dentist dentist = referenceDataDAO.findDentistByStaffUserId(dentistUser.getUserId())
                .orElseThrow(() -> new BusinessRuleException("This user has no dentist profile."));
        appointmentDAO.recordTreatment(appointmentId, dentist.getDentistId(),
                normalizedDiagnosis, normalizedNotes);
    }

    public List<Dentist> activeDentists() {
        return referenceDataDAO.findActiveDentists();
    }

    public List<TreatmentType> activeTreatmentTypes() {
        return referenceDataDAO.findTreatmentTypes(true);
    }

    private void requireRole(StaffUser user, StaffRole role) {
        if (user == null || !user.hasRole(role)) {
            throw new SecurityException("Your account is not authorized for this operation.");
        }
    }
}
