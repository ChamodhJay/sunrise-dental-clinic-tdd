package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.Dentist;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TreatmentRecordingTest {
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
    public void assignedDentistProfileIsUsedToRecordValidTreatment() {
        StaffUser dentistUser = user(StaffRole.DENTIST, true);
        Dentist dentist = new Dentist(UUID.randomUUID(), dentistUser, true);
        UUID appointmentId = UUID.randomUUID();
        when(referenceDataDAO.findDentistByStaffUserId(dentistUser.getUserId()))
                .thenReturn(Optional.of(dentist));

        appointmentService.recordTreatment(
                appointmentId, dentistUser, "Dental caries", "Composite filling completed");

        verify(appointmentDAO).recordTreatment(
                appointmentId,
                dentist.getDentistId(),
                "Dental caries",
                "Composite filling completed");
    }

    @Test
    public void receptionistCannotRecordTreatment() {
        StaffUser receptionist = user(StaffRole.RECEPTIONIST, true);

        assertThrows(SecurityException.class, () -> appointmentService.recordTreatment(
                UUID.randomUUID(), receptionist, "Diagnosis", "Treatment notes"));

        verifyNoInteractions(referenceDataDAO, appointmentDAO);
    }

    @Test
    public void inactiveDentistCannotRecordTreatment() {
        StaffUser inactiveDentist = user(StaffRole.DENTIST, false);

        assertThrows(SecurityException.class, () -> appointmentService.recordTreatment(
                UUID.randomUUID(), inactiveDentist, "Diagnosis", "Treatment notes"));

        verifyNoInteractions(referenceDataDAO, appointmentDAO);
    }

    @Test
    public void blankDiagnosisIsRejectedBeforeDatabaseAccess() {
        StaffUser dentistUser = user(StaffRole.DENTIST, true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.recordTreatment(
                        UUID.randomUUID(), dentistUser, "  ", "Treatment notes"));

        assertEquals(
                "Diagnosis and treatment notes are required and must fit the stated length limits.",
                exception.getMessage());
        verify(referenceDataDAO, never()).findDentistByStaffUserId(dentistUser.getUserId());
        verifyNoInteractions(appointmentDAO);
    }

    @Test
    public void dentistWithoutProfileCannotRecordTreatment() {
        StaffUser dentistUser = user(StaffRole.DENTIST, true);
        when(referenceDataDAO.findDentistByStaffUserId(dentistUser.getUserId()))
                .thenReturn(Optional.empty());

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.recordTreatment(
                        UUID.randomUUID(), dentistUser, "Diagnosis", "Treatment notes"));

        assertEquals("This user has no dentist profile.", exception.getMessage());
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
