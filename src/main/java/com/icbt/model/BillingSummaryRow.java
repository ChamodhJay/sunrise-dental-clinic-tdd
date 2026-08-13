package com.icbt.model;

import java.math.BigDecimal;

/**
 * Read-only billing report projection.
 *
 * <p>The explicit JavaBean getters are required because the JSP/EL version
 * supplied by Tomcat 9 resolves bean properties through {@code getXxx()}
 * methods and does not treat Java record component accessors as bean getters.</p>
 */
public record BillingSummaryRow(String treatmentName, long billCount, BigDecimal totalAmount) {
    public String getTreatmentName() {
        return treatmentName;
    }

    public long getBillCount() {
        return billCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
