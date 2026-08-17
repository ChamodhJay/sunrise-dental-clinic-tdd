package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.Appointment;
import com.icbt.model.Dentist;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class AppointmentLookupTest {
    private AppointmentDAO appointmentDAO;
    private ReferenceDataDAO referenceDataDAO;
    private AppointmentService appointmentService;

    @Before
    public void setUp() {
        appointmentDAO = mock(AppointmentDAO.class);
        referenceDataDAO = mock(ReferenceDataDAO.class);
        appointmentService = new AppointmentService(
                appointmentDAO,
                referenceDataDAO,
                new AppointmentValidator(),
                Clock.fixed(Instant.parse("2026-08-17T08:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    public void lookupTrimsAppointmentNumberAndReturnsMatchingAppointment() {
        Appointment expected = mock(Appointment.class);
        when(appointmentDAO.findByNumber("APT-000001")).thenReturn(Optional.of(expected));

        Appointment actual = appointmentService.findByNumber("  APT-000001  ");

        assertSame(expected, actual);
        verify(appointmentDAO).findByNumber("APT-000001");
    }

    @Test
    public void blankAppointmentNumberIsRejectedBeforeDatabaseAccess() {
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.findByNumber("   "));

        assertEquals("Enter an appointment number.", exception.getMessage());
        verifyNoInteractions(appointmentDAO);
    }

    @Test
    public void unknownAppointmentNumberProducesNotFoundError() {
        when(appointmentDAO.findByNumber("APT-999999")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> appointmentService.findByNumber("APT-999999"));

        assertEquals("Appointment not found.", exception.getMessage());
    }

    @Test
    public void dentistReceivesOnlyAppointmentsAssignedToTheirProfile() {
        StaffUser dentistUser = user(StaffRole.DENTIST, true);
        Dentist dentist = new Dentist(UUID.randomUUID(), dentistUser, true);
        List<Appointment> assigned = List.of(mock(Appointment.class), mock(Appointment.class));
        when(referenceDataDAO.findDentistByStaffUserId(dentistUser.getUserId()))
                .thenReturn(Optional.of(dentist));
        when(appointmentDAO.findAssignedToDentist(dentist.getDentistId())).thenReturn(assigned);

        List<Appointment> actual = appointmentService.findAssignedAppointments(dentistUser);

        assertSame(assigned, actual);
        verify(appointmentDAO).findAssignedToDentist(dentist.getDentistId());
    }

    @Test
    public void nonDentistCannotReadAssignedDentistAppointments() {
        StaffUser receptionist = user(StaffRole.RECEPTIONIST, true);

        assertThrows(SecurityException.class,
                () -> appointmentService.findAssignedAppointments(receptionist));

        verify(referenceDataDAO, never()).findDentistByStaffUserId(receptionist.getUserId());
        verifyNoInteractions(appointmentDAO);
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
