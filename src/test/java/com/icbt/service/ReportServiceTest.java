package com.icbt.service;

import com.icbt.dao.BillDAO;
import com.icbt.model.Appointment;
import com.icbt.model.Bill;
import com.icbt.model.BillLine;
import com.icbt.model.BillLineType;
import com.icbt.model.BillStatus;
import com.icbt.model.ClinicFeeSchedule;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for report-oriented operations in {@link BillingService}:
 * retrieving bills for display, marking a bill as printed, and the
 * role-based guards that protect those operations.
 */
public class ReportServiceTest {

    private AppointmentService appointmentService;
    private BillDAO billDAO;
    private BillingService billingService;

    private Appointment appointment;
    private StaffUser receptionist;

    @Before
    public void setUp() {
        appointmentService = mock(AppointmentService.class);
        billDAO            = mock(BillDAO.class);
        billingService     = new BillingService(appointmentService, billDAO);

        appointment  = mock(Appointment.class);
        receptionist = user(StaffRole.RECEPTIONIST, true);

        when(appointment.getAppointmentId()).thenReturn(UUID.randomUUID());
    }

    // ------------------------------------------------------------------ findBill

    @Test
    public void findBillReturnsBillWhenOneExistsForTheAppointment() {
        Bill bill = mockBill();

        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.of(bill));

        Bill actual = billingService.findBill("APT-000001");

        assertSame(bill, actual);
        verify(billDAO).findByAppointment(appointment);
    }

    @Test
    public void findBillThrowsNotFoundWhenNoBillExistsYet() {
        when(appointmentService.findByNumber("APT-888888")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> billingService.findBill("APT-888888"));

        assertEquals("No bill exists for this appointment.", exception.getMessage());
    }

    @Test
    public void findBillDelegatesAppointmentLookupToAppointmentService() {
        Bill bill = mockBill();

        when(appointmentService.findByNumber("APT-000002")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.of(bill));

        billingService.findBill("APT-000002");

        verify(appointmentService).findByNumber("APT-000002");
    }

    @Test
    public void findBillPropagatesNotFoundWhenAppointmentNumberIsUnknown() {
        when(appointmentService.findByNumber("APT-999999"))
                .thenThrow(new NotFoundException("Appointment not found."));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> billingService.findBill("APT-999999"));

        assertEquals("Appointment not found.", exception.getMessage());
        verifyNoInteractions(billDAO);
    }

    // ------------------------------------------------------------------ markPrinted

    @Test
    public void markPrintedUpdatesStatusAndReturnsBillWithPrintedStatus() {
        Bill bill = mockBill();

        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.of(bill));
        when(bill.getBillId()).thenReturn(UUID.randomUUID());
        when(bill.getStatus()).thenReturn(BillStatus.PRINTED);

        Bill printed = billingService.markPrinted("APT-000001", receptionist);

        assertNotNull(printed);
        verify(billDAO).updateStatus(bill.getBillId(), BillStatus.PRINTED);
        verify(bill).markPrinted();
    }

    @Test
    public void onlyReceptionistCanMarkBillPrinted() {
        StaffUser dentist = user(StaffRole.DENTIST, true);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> billingService.markPrinted("APT-000001", dentist));

        assertEquals("Only a receptionist can generate or print patient bills.",
                exception.getMessage());

        verifyNoInteractions(appointmentService);
        verifyNoInteractions(billDAO);
    }

    @Test
    public void nullUserIsRejectedForMarkPrinted() {
        assertThrows(
                SecurityException.class,
                () -> billingService.markPrinted("APT-000001", null));

        verifyNoInteractions(appointmentService);
        verifyNoInteractions(billDAO);
    }

    @Test
    public void inactiveReceptionistCannotMarkBillPrinted() {
        StaffUser inactive = user(StaffRole.RECEPTIONIST, false);

        assertThrows(
                SecurityException.class,
                () -> billingService.markPrinted("APT-000001", inactive));

        verifyNoInteractions(appointmentService);
        verifyNoInteractions(billDAO);
    }

    @Test
    public void markPrintedThrowsNotFoundWhenNoBillExists() {
        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> billingService.markPrinted("APT-000001", receptionist));

        verify(billDAO, never()).updateStatus(any(), any());
    }

    // ------------------------------------------------------------------ Bill content assertions

    @Test
    public void billLinesArePresentAndAccessible() {
        Bill bill = realBill();

        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.of(bill));

        Bill found = billingService.findBill("APT-000001");

        assertEquals(2, found.getLines().size());
        assertNotNull(found.getTotalAmount());
        assertEquals(0, new BigDecimal("2750.00").compareTo(found.getTotalAmount()));
    }

    @Test
    public void billNumberIsPreservedInReturnedBill() {
        Bill bill = mockBill();

        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment);
        when(billDAO.findByAppointment(appointment)).thenReturn(Optional.of(bill));
        when(bill.getBillNumber()).thenReturn("BILL-000001");

        Bill found = billingService.findBill("APT-000001");

        assertEquals("BILL-000001", found.getBillNumber());
    }

    // ------------------------------------------------------------------ helpers

    private Bill mockBill() {
        return mock(Bill.class);
    }

    /**
     * Constructs a real {@link Bill} with two lines so that line-count and
     * total-amount assertions work without mocking every getter.
     */
    private Bill realBill() {
        ClinicFeeSchedule fee = new ClinicFeeSchedule(
                UUID.randomUUID(),
                new BigDecimal("750.00"),
                LocalDate.of(2026, 1, 1),
                true);

        Bill bill = new Bill(
                UUID.randomUUID(),
                "BILL-000001",
                appointment,
                user(StaffRole.RECEPTIONIST, true),
                fee,
                new BigDecimal("2750.00"),
                BillStatus.CREATED,
                LocalDateTime.of(2026, 8, 19, 10, 0),
                List.of(
                        new BillLine(UUID.randomUUID(), BillLineType.CONSULTATION,
                                "Consultation Fee", new BigDecimal("750.00")),
                        new BillLine(UUID.randomUUID(), BillLineType.TREATMENT,
                                "Teeth Whitening", new BigDecimal("2000.00"))));
        return bill;
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

    /** Convenience alias so the Mockito static import stays readable. */
    private static <T> T any() {
        return org.mockito.ArgumentMatchers.any();
    }
}
