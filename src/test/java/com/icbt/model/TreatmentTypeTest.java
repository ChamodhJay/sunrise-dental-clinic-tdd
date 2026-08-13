package com.icbt.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TreatmentTypeTest {
    @Test
    public void constructorStoresValuesAndNormalizesPriceToTwoDecimalPlaces() {
        UUID id = UUID.randomUUID();

        TreatmentType treatment = new TreatmentType(
                id, "Dental Cleaning", new BigDecimal("1500"), true);

        assertEquals(id, treatment.getTreatmentTypeId());
        assertEquals("Dental Cleaning", treatment.getName());
        assertEquals(new BigDecimal("1500.00"), treatment.getBasePrice());
        assertEquals(new BigDecimal("1500.00"), treatment.currentPrice());
        assertTrue(treatment.isActive());
    }

    @Test
    public void changePriceReplacesCurrentPrice() {
        TreatmentType treatment = treatmentWithPrice("1500.00", true);

        treatment.changePrice(new BigDecimal("1750"));

        assertEquals(new BigDecimal("1750.00"), treatment.getBasePrice());
        assertEquals(new BigDecimal("1750.00"), treatment.currentPrice());
    }

    @Test
    public void zeroPriceIsAllowed() {
        TreatmentType treatment = treatmentWithPrice("0", true);

        assertEquals(new BigDecimal("0.00"), treatment.currentPrice());
    }

    @Test
    public void nullAndNegativePricesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> treatmentWithPrice(null, true));
        assertThrows(IllegalArgumentException.class,
                () -> treatmentWithPrice("-0.01", true));

        TreatmentType treatment = treatmentWithPrice("1500.00", true);
        assertThrows(IllegalArgumentException.class,
                () -> treatment.changePrice(null));
        assertThrows(IllegalArgumentException.class,
                () -> treatment.changePrice(new BigDecimal("-0.01")));
    }

    @Test
    public void identityAndNameAreRequired() {
        assertThrows(NullPointerException.class,
                () -> new TreatmentType(null, "Dental Cleaning", new BigDecimal("1500.00"), true));
        assertThrows(NullPointerException.class,
                () -> new TreatmentType(UUID.randomUUID(), null, new BigDecimal("1500.00"), true));
    }

    @Test
    public void treatmentCanBeActivatedAndDeactivated() {
        TreatmentType treatment = treatmentWithPrice("1500.00", false);
        assertFalse(treatment.isActive());

        treatment.activate();
        assertTrue(treatment.isActive());

        treatment.deactivate();
        assertFalse(treatment.isActive());
    }

    private TreatmentType treatmentWithPrice(String price, boolean active) {
        BigDecimal amount = price == null ? null : new BigDecimal(price);
        return new TreatmentType(UUID.randomUUID(), "Dental Cleaning", amount, active);
    }
}
