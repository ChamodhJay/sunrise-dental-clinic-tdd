package com.icbt.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RegisterAppointmentCommand(
        String patientName,
        String address,
        String contactNumber,
        UUID dentistId,
        UUID treatmentTypeId,
        LocalDate appointmentDate,
        LocalTime appointmentTime) {
}
