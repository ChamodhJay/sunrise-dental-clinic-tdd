package com.icbt.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class BillLine {
    private final UUID billLineId;
    private final BillLineType lineType;
    private final String description;
    private final BigDecimal amount;

    public BillLine(UUID billLineId, BillLineType lineType, String description, BigDecimal amount) {
        this.billLineId = Objects.requireNonNull(billLineId);
        this.lineType = Objects.requireNonNull(lineType);
        this.description = Objects.requireNonNull(description);
        this.amount = Objects.requireNonNull(amount).setScale(2);
        if (!isValid()) {
            throw new IllegalArgumentException("A bill line requires a description and non-negative amount");
        }
    }

    public UUID getBillLineId() { return billLineId; }
    public BillLineType getLineType() { return lineType; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public boolean isValid() { return !description.isBlank() && amount.signum() >= 0; }
}
