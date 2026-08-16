package com.icbt.service;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AppointmentValidatorTest {
    private final AppointmentValidator validator = new AppointmentValidator();

    @Test
    public void acceptsValidFutureWeekdayHalfHourSlot() {
        RegisterAppointmentCommand command = new RegisterAppointmentCommand(
                "Nimal Perera", "10 Galle Road, Colombo", "0771234567",
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 8, 13), LocalTime.of(9, 30));
        validator.validate(command, LocalDate.of(2026, 8, 12));
    }

    @Test
    public void rejectsWeekendAndNonHalfHourTime() {
        RegisterAppointmentCommand command = new RegisterAppointmentCommand(
                "Nimal Perera", "10 Galle Road, Colombo", "0771234567",
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 8, 15), LocalTime.of(10, 15));
        try {
            validator.validate(command, LocalDate.of(2026, 8, 12));
            fail("Expected validation to fail");
        } catch (ValidationException exception) {
            assertTrue(exception.getFieldErrors().containsKey("appointmentDate"));
            assertTrue(exception.getFieldErrors().containsKey("appointmentTime"));
        }
    }

    @Test
    public void rejectsMalformedPatientAndPhone() {
        RegisterAppointmentCommand command = new RegisterAppointmentCommand(
                "1", "", "12345", null, null, null, null);
        try {
            validator.validate(command, LocalDate.of(2026, 8, 12));
            fail("Expected validation to fail");
        } catch (ValidationException exception) {
            assertTrue(exception.getFieldErrors().size() >= 6);
        }
    }
}
