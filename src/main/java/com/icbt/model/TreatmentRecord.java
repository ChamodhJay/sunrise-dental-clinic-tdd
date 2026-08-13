package com.icbt.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TreatmentRecord {
    private final UUID treatmentRecordId;
    private final Dentist recordedBy;
    private final String diagnosis;
    private final String treatmentNotes;
    private final LocalDateTime completedAt;
    private final LocalDateTime createdAt;

    public TreatmentRecord(UUID treatmentRecordId, Dentist recordedBy, String diagnosis,
                           String treatmentNotes, LocalDateTime completedAt, LocalDateTime createdAt) {
        this.treatmentRecordId = Objects.requireNonNull(treatmentRecordId);
        this.recordedBy = Objects.requireNonNull(recordedBy);
        this.diagnosis = Objects.requireNonNull(diagnosis);
        this.treatmentNotes = Objects.requireNonNull(treatmentNotes);
        this.completedAt = Objects.requireNonNull(completedAt);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID getTreatmentRecordId() { return treatmentRecordId; }
    public Dentist getRecordedBy() { return recordedBy; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatmentNotes() { return treatmentNotes; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean validate() { return !diagnosis.isBlank() && !treatmentNotes.isBlank(); }
}
