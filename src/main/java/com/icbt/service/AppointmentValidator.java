package com.icbt.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class AppointmentValidator {
    private static final Pattern NAME = Pattern.compile("[\\p{L} .'-]{2,100}");
    private static final Pattern PHONE = Pattern.compile("(?:\\+94\\d{9}|0\\d{9})");
    private static final LocalTime OPENING = LocalTime.of(9, 0);
    private static final LocalTime LAST_SLOT = LocalTime.of(18, 0);

    public void validate(RegisterAppointmentCommand command, LocalDate today) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (command.patientName() == null || !NAME.matcher(command.patientName().trim()).matches()) {
            errors.put("patientName", "Enter a valid patient name (2-100 letters). ");
        }
        if (command.address() == null || command.address().trim().isEmpty() || command.address().length() > 200) {
            errors.put("address", "Address is required and must not exceed 200 characters.");
        }
        String phone = command.contactNumber() == null ? "" : command.contactNumber().replace(" ", "");
        if (!PHONE.matcher(phone).matches()) {
            errors.put("contactNumber", "Use +94XXXXXXXXX or 0XXXXXXXXX.");
        }
        if (command.dentistId() == null) {
            errors.put("dentistId", "Select a dentist.");
        }
        if (command.treatmentTypeId() == null) {
            errors.put("treatmentTypeId", "Select a treatment type.");
        }
        if (command.appointmentDate() == null || !command.appointmentDate().isAfter(today)) {
            errors.put("appointmentDate", "Appointment date must be in the future.");
        } else if (command.appointmentDate().getDayOfWeek() == DayOfWeek.SATURDAY
                || command.appointmentDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            errors.put("appointmentDate", "Appointments are available Monday to Friday only.");
        }
        LocalTime time = command.appointmentTime();
        if (time == null || time.isBefore(OPENING) || time.isAfter(LAST_SLOT)
                || (time.getMinute() != 0 && time.getMinute() != 30) || time.getSecond() != 0) {
            errors.put("appointmentTime", "Choose a 30-minute slot from 09:00 through 18:00.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
