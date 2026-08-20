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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
        String treatmentId = UUID.randomUUID().toString();
        String name = "Root Canal";
        String price = "4500.00";

        catalogueService.saveTreatment(manager, treatmentId, name, price, true);

        verify(referenceDataDAO).saveTreatment(UUID.fromString(treatmentId), name, new BigDecimal(price), true, manager);
    }

    @Test
    public void onlyManagerCanSaveTreatment() {
        StaffUser dentist = user(StaffRole.DENTIST, true);
        String treatmentId = UUID.randomUUID().toString();
        
        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> catalogueService.saveTreatment(dentist, treatmentId, "Clean", "100.00", true));

        assertEquals("Only the clinic manager can maintain catalogue and fee data.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    // ------------------------------------------------------------------ changeConsultationFee

    @Test
    public void changeConsultationFeeReturnsNewFeeSchedule() {
        String fee = "1500.00";
        ClinicFeeSchedule schedule = mock(ClinicFeeSchedule.class);

        when(referenceDataDAO.replaceActiveFee(eq(new BigDecimal(fee)), any(LocalDate.class), eq(manager)))
                .thenReturn(schedule);

        ClinicFeeSchedule result = catalogueService.changeConsultationFee(manager, fee);

        assertSame(schedule, result);
        verify(referenceDataDAO).replaceActiveFee(eq(new BigDecimal(fee)), any(LocalDate.class), eq(manager));
    }

    @Test
    public void feeCannotBeNegative() {
        String fee = "-100.00";

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> catalogueService.changeConsultationFee(manager, fee));

        assertEquals("Enter a non-negative consultation fee.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    @Test
    public void feeOutsideDatabaseDecimalRangeIsRejectedBeforeJdbc() {
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> catalogueService.changeConsultationFee(manager, "10000000000.00"));

        assertEquals("Enter a non-negative consultation fee.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    @Test
    public void onlyManagerCanChangeConsultationFee() {
        StaffUser receptionist = user(StaffRole.RECEPTIONIST, true);
        String fee = "1500.00";

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> catalogueService.changeConsultationFee(receptionist, fee));

        assertEquals("Only the clinic manager can maintain catalogue and fee data.", exception.getMessage());
        verifyNoInteractions(referenceDataDAO);
    }

    // ------------------------------------------------------------------ Queries

    @Test
    public void treatmentsDelegatesToDao() {
        List<TreatmentType> expected = List.of(mock(TreatmentType.class));
        when(referenceDataDAO.findTreatmentTypes(false)).thenReturn(expected);

        List<TreatmentType> actual = catalogueService.treatments(manager);

        assertSame(expected, actual);
        verify(referenceDataDAO).findTreatmentTypes(false);
    }

    @Test
    public void activeFeeDelegatesToDao() {
        ClinicFeeSchedule expected = mock(ClinicFeeSchedule.class);
        when(referenceDataDAO.findActiveFeeSchedule()).thenReturn(Optional.of(expected));

        ClinicFeeSchedule actual = catalogueService.activeFee(manager);

        assertSame(expected, actual);
        verify(referenceDataDAO).findActiveFeeSchedule();
    }
    
    @Test
    public void activeFeeThrowsWhenNoneExists() {
        when(referenceDataDAO.findActiveFeeSchedule()).thenReturn(Optional.empty());

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> catalogueService.activeFee(manager));
                
        assertEquals("No active consultation fee is configured.", exception.getMessage());
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
