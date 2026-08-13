package com.icbt.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ClinicFeeScheduleTest {
    private static final LocalDate EFFECTIVE_FROM = LocalDate.of(2026, 8, 1);

    @Test
    public void constructorStoresValuesAndNormalizesFeeToTwoDecimalPlaces() {
        UUID id = UUID.randomUUID();

        ClinicFeeSchedule schedule = new ClinicFeeSchedule(
                id, new BigDecimal("300"), EFFECTIVE_FROM, true);

        assertEquals(id, schedule.getFeeScheduleId());
        assertEquals(new BigDecimal("300.00"), schedule.getConsultationFee());
        assertEquals(new BigDecimal("300.00"), schedule.currentConsultationFee());
        assertEquals(EFFECTIVE_FROM, schedule.getEffectiveFrom());
        assertTrue(schedule.isActive());
    }

    @Test
    public void changeConsultationFeeReplacesCurrentFee() {
        ClinicFeeSchedule schedule = scheduleWithFee("300.00", true);

        schedule.changeConsultationFee(new BigDecimal("450"));

        assertEquals(new BigDecimal("450.00"), schedule.getConsultationFee());
        assertEquals(new BigDecimal("450.00"), schedule.currentConsultationFee());
    }

    @Test
    public void nullAndNegativeFeesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> scheduleWithFee(null, true));
        assertThrows(IllegalArgumentException.class,
                () -> scheduleWithFee("-0.01", true));

        ClinicFeeSchedule schedule = scheduleWithFee("300.00", true);
        assertThrows(IllegalArgumentException.class,
                () -> schedule.changeConsultationFee(null));
        assertThrows(IllegalArgumentException.class,
                () -> schedule.changeConsultationFee(new BigDecimal("-0.01")));
    }

    @Test
    public void identityAndEffectiveDateAreRequired() {
        assertThrows(NullPointerException.class,
                () -> new ClinicFeeSchedule(null, new BigDecimal("300.00"), EFFECTIVE_FROM, true));
        assertThrows(NullPointerException.class,
                () -> new ClinicFeeSchedule(UUID.randomUUID(), new BigDecimal("300.00"), null, true));
    }

    @Test
    public void scheduleCanBeActivatedAndDeactivated() {
        ClinicFeeSchedule schedule = scheduleWithFee("300.00", false);
        assertFalse(schedule.isActive());

        schedule.activate();
        assertTrue(schedule.isActive());

        schedule.deactivate();
        assertFalse(schedule.isActive());
    }

    private ClinicFeeSchedule scheduleWithFee(String fee, boolean active) {
        BigDecimal amount = fee == null ? null : new BigDecimal(fee);
        return new ClinicFeeSchedule(UUID.randomUUID(), amount, EFFECTIVE_FROM, active);
    }
}
