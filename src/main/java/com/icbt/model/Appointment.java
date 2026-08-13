package com.icbt.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public final class Appointment {
    private final UUID appointmentId;
    private final String appointmentNumber;
    private final Patient patient;
    private final Dentist dentist;
    private final TreatmentType treatmentType;
    private final StaffUser registeredBy;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private AppointmentStatus status;
    private final LocalDateTime createdAt;
    private TreatmentRecord treatmentRecord;

    public Appointment(UUID appointmentId, String appointmentNumber, Patient patient, Dentist dentist,
                       TreatmentType treatmentType, StaffUser registeredBy, LocalDate appointmentDate,
                       LocalTime appointmentTime, AppointmentStatus status, LocalDateTime createdAt,
                       TreatmentRecord treatmentRecord) {
        this.appointmentId = Objects.requireNonNull(appointmentId);
        this.appointmentNumber = Objects.requireNonNull(appointmentNumber);
        this.patient = Objects.requireNonNull(patient);
        this.dentist = Objects.requireNonNull(dentist);
        this.treatmentType = Objects.requireNonNull(treatmentType);
        this.registeredBy = Objects.requireNonNull(registeredBy);
        this.appointmentDate = Objects.requireNonNull(appointmentDate);
        this.appointmentTime = Objects.requireNonNull(appointmentTime);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.treatmentRecord = treatmentRecord;
    }

    public UUID getAppointmentId() { return appointmentId; }
    public String getAppointmentNumber() { return appointmentNumber; }
    public Patient getPatient() { return patient; }
    public Dentist getDentist() { return dentist; }
    public TreatmentType getTreatmentType() { return treatmentType; }
    public StaffUser getRegisteredBy() { return registeredBy; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TreatmentRecord getTreatmentRecord() { return treatmentRecord; }

    public void schedule() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only a new appointment can be scheduled");
        }
    }

    public void complete(TreatmentRecord record) {
        if (status != AppointmentStatus.SCHEDULED || treatmentRecord != null) {
            throw new IllegalStateException("Appointment has already been completed");
        }
        this.treatmentRecord = Objects.requireNonNull(record);
        this.status = AppointmentStatus.COMPLETED;
    }

    public boolean occursAt(LocalDate date, LocalTime time) {
        return appointmentDate.equals(date) && appointmentTime.equals(time);
    }
}
