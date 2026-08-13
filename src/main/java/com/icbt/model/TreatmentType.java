package com.icbt.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class TreatmentType {
    private final UUID treatmentTypeId;
    private final String name;
    private BigDecimal basePrice;
    private boolean active;

    public TreatmentType(UUID treatmentTypeId, String name, BigDecimal basePrice, boolean active) {
        this.treatmentTypeId = Objects.requireNonNull(treatmentTypeId);
        this.name = Objects.requireNonNull(name);
        changePrice(basePrice);
        this.active = active;
    }

    public UUID getTreatmentTypeId() { return treatmentTypeId; }
    public String getName() { return name; }
    public BigDecimal getBasePrice() { return basePrice; }
    public boolean isActive() { return active; }
    public BigDecimal currentPrice() { return basePrice; }

    public void changePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.signum() < 0) {
            throw new IllegalArgumentException("Treatment price must not be negative");
        }
        this.basePrice = newPrice.setScale(2);
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
}
