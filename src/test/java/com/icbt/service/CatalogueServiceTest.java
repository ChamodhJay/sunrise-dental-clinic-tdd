package com.icbt.service;

import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.ClinicFeeSchedule;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

/**
 * Tests for CatalogueService which manages treatments and clinic fees.
 * Applies TDD principles as the CatalogueService class itself will be
 * driven by these tests.
 */
public class CatalogueServiceTest {

    private ReferenceDataDAO referenceDataDAO;
    private CatalogueService catalogueService;
    private StaffUser manager;

    @Before
    public void setUp() {
        referenceDataDAO = mock(ReferenceDataDAO.class);
        catalogueService = new CatalogueService(referenceDataDAO);
        manager = user(StaffRole.CLINIC_MANAGER, true);
    }

    // ------------------------------------------------------------------ saveTreatment

    @Test
    public void saveTreatmentDelegatesToDaoWhenUserIsManager() {
        UUID treatmentId = UUID.randomUUID();
        String name = "Root Canal";
        BigDecimal price = new BigDecimal("4500.00");

        catalogueService.saveTreatment(manager, treatmentId, name, price, true);

        verify(referenceDataDAO).saveTreatment(treatmentId, name, price, true, manager);
    }

    @Test
    public void onlyManagerCanSaveTreatment() {
        StaffUser dentist = user(StaffRole.DENTIST, true);
        UUID treatmentId = UUID.randomUUID();
        
        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> catalogueService.saveTreatment(dentist, treatmentId, "Clean", new BigDecimal("100"), true));

        assertEquals("Only an active clinic manager may maintain treatments.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    // ------------------------------------------------------------------ updateConsultationFee

    @Test
    public void updateConsultationFeeReturnsNewFeeSchedule() {
        BigDecimal fee = new BigDecimal("1500.00");
        LocalDate effectiveFrom = LocalDate.of(2026, 9, 1);
        ClinicFeeSchedule schedule = mock(ClinicFeeSchedule.class);

        when(referenceDataDAO.replaceActiveFee(fee, effectiveFrom, manager)).thenReturn(schedule);

        ClinicFeeSchedule result = catalogueService.updateConsultationFee(manager, fee, effectiveFrom);

        assertSame(schedule, result);
        verify(referenceDataDAO).replaceActiveFee(fee, effectiveFrom, manager);
    }

    @Test
    public void feeCannotBeNegative() {
        BigDecimal fee = new BigDecimal("-100.00");
        LocalDate effectiveFrom = LocalDate.now();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> catalogueService.updateConsultationFee(manager, fee, effectiveFrom));

        assertEquals("Consultation fee must not be negative", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    @Test
    public void onlyManagerCanUpdateConsultationFee() {
        StaffUser receptionist = user(StaffRole.RECEPTIONIST, true);
        BigDecimal fee = new BigDecimal("1500.00");

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> catalogueService.updateConsultationFee(receptionist, fee, LocalDate.now()));

        assertEquals("Only an active clinic manager may change fees.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    // ------------------------------------------------------------------ Queries

    @Test
    public void findActiveTreatmentTypesDelegatesToDao() {
        List<TreatmentType> expected = List.of(mock(TreatmentType.class));
        when(referenceDataDAO.findTreatmentTypes(true)).thenReturn(expected);

        List<TreatmentType> actual = catalogueService.activeTreatmentTypes();

        assertSame(expected, actual);
        verify(referenceDataDAO).findTreatmentTypes(true);
    }

    @Test
    public void findActiveFeeScheduleDelegatesToDao() {
        ClinicFeeSchedule expected = mock(ClinicFeeSchedule.class);
        when(referenceDataDAO.findActiveFeeSchedule()).thenReturn(Optional.of(expected));

        Optional<ClinicFeeSchedule> actual = catalogueService.activeFee();

        assertSame(expected, actual.orElse(null));
        verify(referenceDataDAO).findActiveFeeSchedule();
    }
    
    // ------------------------------------------------------------------ helpers

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
