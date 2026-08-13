package com.icbt.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Bill {
    private final UUID billId;
    private final String billNumber;
    private final Appointment appointment;
    private final StaffUser generatedBy;
    private final ClinicFeeSchedule feeSchedule;
    private final List<BillLine> lines = new ArrayList<>();
    private BigDecimal totalAmount;
    private BillStatus status;
    private final LocalDateTime generatedAt;

    public Bill(UUID billId, String billNumber, Appointment appointment, StaffUser generatedBy,
                ClinicFeeSchedule feeSchedule, BigDecimal totalAmount, BillStatus status,
                LocalDateTime generatedAt, List<BillLine> initialLines) {
        this.billId = Objects.requireNonNull(billId);
        this.billNumber = Objects.requireNonNull(billNumber);
        this.appointment = Objects.requireNonNull(appointment);
        this.generatedBy = Objects.requireNonNull(generatedBy);
        this.feeSchedule = Objects.requireNonNull(feeSchedule);
        this.totalAmount = Objects.requireNonNull(totalAmount).setScale(2);
        this.status = Objects.requireNonNull(status);
        this.generatedAt = Objects.requireNonNull(generatedAt);
        if (initialLines != null) {
            this.lines.addAll(initialLines);
        }
    }

    public UUID getBillId() { return billId; }
    public String getBillNumber() { return billNumber; }
    public Appointment getAppointment() { return appointment; }
    public StaffUser getGeneratedBy() { return generatedBy; }
    public ClinicFeeSchedule getFeeSchedule() { return feeSchedule; }
    public List<BillLine> getLines() { return Collections.unmodifiableList(lines); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BillStatus getStatus() { return status; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void addLine(BillLineType type, String description, BigDecimal amount) {
        if (lines.stream().anyMatch(line -> line.getLineType() == type)) {
            throw new IllegalStateException("A bill cannot contain duplicate line types");
        }
        lines.add(new BillLine(UUID.randomUUID(), type, description, amount));
        totalAmount = calculateTotal();
    }

    public BigDecimal calculateTotal() {
        return lines.stream().map(BillLine::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    public boolean hasRequiredLines() {
        EnumSet<BillLineType> types = EnumSet.noneOf(BillLineType.class);
        lines.forEach(line -> types.add(line.getLineType()));
        return lines.size() == 2 && types.equals(EnumSet.allOf(BillLineType.class))
                && totalAmount.compareTo(calculateTotal()) == 0;
    }

    public void markPrinted() { status = BillStatus.PRINTED; }
    public void markPrintFailed() { status = BillStatus.PRINT_FAILED; }
}
