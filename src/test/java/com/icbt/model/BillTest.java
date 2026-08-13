package com.icbt.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BillTest {
    @Test
    public void totalIsTreatmentPlusConsultationAndContainsExactlyTwoTypes() {
        Fixture fixture = fixture();
        Bill bill = new Bill(UUID.randomUUID(), "PENDING", fixture.appointment,
                fixture.receptionist, fixture.fee, BigDecimal.ZERO.setScale(2),
                BillStatus.CREATED, LocalDateTime.now(), null);
        bill.addLine(BillLineType.TREATMENT, "Cleaning", new BigDecimal("1500.00"));
        bill.addLine(BillLineType.CONSULTATION, "Consultation fee", new BigDecimal("300.00"));
        assertEquals(new BigDecimal("1800.00"), bill.calculateTotal());
        assertEquals(new BigDecimal("1800.00"), bill.getTotalAmount());
        assertTrue(bill.hasRequiredLines());
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateBillLineTypeIsRejected() {
        Fixture fixture = fixture();
        Bill bill = new Bill(UUID.randomUUID(), "PENDING", fixture.appointment,
                fixture.receptionist, fixture.fee, BigDecimal.ZERO.setScale(2),
                BillStatus.CREATED, LocalDateTime.now(), null);
        bill.addLine(BillLineType.TREATMENT, "Cleaning", new BigDecimal("1500.00"));
        bill.addLine(BillLineType.TREATMENT, "Duplicate", new BigDecimal("1.00"));
    }

    private Fixture fixture() {
        LocalDateTime now = LocalDateTime.now();
        StaffUser receptionist = new StaffUser(UUID.randomUUID(), "reception", "hash",
                "Receptionist", StaffRole.RECEPTIONIST, true, now);
        StaffUser dentistUser = new StaffUser(UUID.randomUUID(), "dentist", "hash",
                "Dentist", StaffRole.DENTIST, true, now);
        Dentist dentist = new Dentist(UUID.randomUUID(), dentistUser, true);
        Patient patient = new Patient(UUID.randomUUID(), "Patient", "0771234567", "Address", now);
        TreatmentType treatment = new TreatmentType(UUID.randomUUID(), "Cleaning",
                new BigDecimal("1500.00"), true);
        Appointment appointment = new Appointment(UUID.randomUUID(), "APT-260813-0001", patient,
                dentist, treatment, receptionist, LocalDate.of(2026, 8, 13), LocalTime.of(9, 30),
                AppointmentStatus.COMPLETED, now, null);
        ClinicFeeSchedule fee = new ClinicFeeSchedule(UUID.randomUUID(), new BigDecimal("300.00"),
                LocalDate.of(2026, 8, 1), true);
        return new Fixture(appointment, receptionist, fee);
    }

    private record Fixture(Appointment appointment, StaffUser receptionist, ClinicFeeSchedule fee) { }
}
