package com.icbt.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class ClinicFeeSchedule {
    private final UUID feeScheduleId;
    private BigDecimal consultationFee;
    private final LocalDate effectiveFrom;
    private boolean active;

    public ClinicFeeSchedule(UUID feeScheduleId, BigDecimal consultationFee,
                             LocalDate effectiveFrom, boolean active) {
        this.feeScheduleId = Objects.requireNonNull(feeScheduleId);
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom);
        changeConsultationFee(consultationFee);
        this.active = active;
    }

    public UUID getFeeScheduleId() { return feeScheduleId; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public boolean isActive() { return active; }
    public BigDecimal currentConsultationFee() { return consultationFee; }

    public void changeConsultationFee(BigDecimal newFee) {
        if (newFee == null || newFee.signum() < 0) {
            throw new IllegalArgumentException("Consultation fee must not be negative");
        }
        this.consultationFee = newFee.setScale(2);
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
}
