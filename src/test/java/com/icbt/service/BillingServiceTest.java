package com.icbt.service;

import com.icbt.dao.BillDAO;
import com.icbt.model.Appointment;
import com.icbt.model.AppointmentStatus;
import com.icbt.model.Bill;
import com.icbt.model.BillLine;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentRecord;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BillingServiceTest {

    private AppointmentService appointmentService;
    private BillDAO billDAO;
    private BillingService billingService;

    private Appointment appointment;
    private StaffUser receptionist;

    @Before
    public void setUp() {
        appointmentService = mock(AppointmentService.class);
        billDAO = mock(BillDAO.class);

        billingService = new BillingService(
                appointmentService,
                billDAO);

        appointment = mock(Appointment.class);
        receptionist = user(StaffRole.RECEPTIONIST, true);

        TreatmentRecord treatmentRecord = mock(TreatmentRecord.class);

        when(appointment.getStatus())
                .thenReturn(AppointmentStatus.COMPLETED);

        when(appointment.getTreatmentRecord())
                .thenReturn(treatmentRecord);
    }

    @Test
    public void onlyCompletedAppointmentsCanBeBilled() {
        when(appointmentService.findByNumber("APT-000001"))
                .thenReturn(appointment);

        when(appointment.getStatus())
                .thenReturn(AppointmentStatus.SCHEDULED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> billingService.calculateBill(
                        "APT-000001",
                        receptionist));

        assertEquals(
                "A bill can be generated only after the dentist records treatment.",
                exception.getMessage());

        verify(billDAO, never())
                .createForCompletedAppointment(appointment, receptionist);
    }

    @Test
    public void onlyReceptionistCanGenerateBill() {
        StaffUser dentist = user(StaffRole.DENTIST, true);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> billingService.calculateBill(
                        "APT-000001",
                        dentist));

        assertEquals(
                "Only a receptionist can generate or print patient bills.",
                exception.getMessage());

        verifyNoInteractions(appointmentService);
        verifyNoInteractions(billDAO);
    }

    @Test
    public void existingBillIsReused() {
        Bill existingBill = mock(Bill.class);

        when(appointmentService.findByNumber("APT-000001"))
                .thenReturn(appointment);

        when(billDAO.findByAppointment(appointment))
                .thenReturn(Optional.of(existingBill));

        Bill actual = billingService.calculateBill(
                "APT-000001",
                receptionist);

        assertSame(existingBill, actual);

        verify(billDAO).findByAppointment(appointment);

        verify(billDAO, never())
                .createForCompletedAppointment(
                        appointment,
                        receptionist);
    }

    @Test
    public void exactlyTwoLinesAreGenerated() {
        Bill bill = mock(Bill.class);

        BillLine firstLine = mock(BillLine.class);
        BillLine secondLine = mock(BillLine.class);

        List<BillLine> lines = Arrays.asList(
                firstLine,
                secondLine);

        when(appointmentService.findByNumber("APT-000001"))
                .thenReturn(appointment);

        when(billDAO.findByAppointment(appointment))
                .thenReturn(Optional.empty());

        when(billDAO.createForCompletedAppointment(
                appointment,
                receptionist))
                .thenReturn(bill);

        when(bill.getLines())
                .thenReturn(lines);

        Bill actual = billingService.calculateBill(
                "APT-000001",
                receptionist);

        assertNotNull(actual);
        assertEquals(2, actual.getLines().size());

        verify(billDAO).createForCompletedAppointment(
                appointment,
                receptionist);
    }

    @Test
    public void totalIsCorrect() {
        Bill bill = mock(Bill.class);

        BillLine consultationLine = mock(BillLine.class);
        BillLine treatmentLine = mock(BillLine.class);

        when(consultationLine.getAmount())
                .thenReturn(new BigDecimal("1000.00"));

        when(treatmentLine.getAmount())
                .thenReturn(new BigDecimal("500.00"));

        List<BillLine> lines = Arrays.asList(
                consultationLine,
                treatmentLine);

        when(bill.getLines())
                .thenReturn(lines);

        when(bill.getTotalAmount())
                .thenReturn(new BigDecimal("1500.00"));

        when(appointmentService.findByNumber("APT-000001"))
                .thenReturn(appointment);

        when(billDAO.findByAppointment(appointment))
                .thenReturn(Optional.empty());

        when(billDAO.createForCompletedAppointment(
                appointment,
                receptionist))
                .thenReturn(bill);

        Bill actual = billingService.calculateBill(
                "APT-000001",
                receptionist);

        assertNotNull(actual);

        assertEquals(
                0,
                new BigDecimal("1500.00")
                        .compareTo(actual.getTotalAmount()));

        verify(billDAO).createForCompletedAppointment(
                appointment,
                receptionist);
    }

    private StaffUser user(StaffRole role, boolean active) {
        return new StaffUser(
                UUID.randomUUID(),
                role.name().toLowerCase(),
                "not-used-by-this-test",
                "Test " + role.name(),
                role,
                active,
                LocalDateTime.of(2026, 8, 1, 9, 0));
    }
}

