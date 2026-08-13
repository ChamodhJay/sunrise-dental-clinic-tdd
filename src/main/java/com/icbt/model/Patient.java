package com.icbt.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Patient {
    private final UUID patientId;
    private String fullName;
    private String contactNumber;
    private String address;
    private final LocalDateTime createdAt;

    public Patient(UUID patientId, String fullName, String contactNumber, String address,
                   LocalDateTime createdAt) {
        this.patientId = Objects.requireNonNull(patientId);
        this.fullName = Objects.requireNonNull(fullName);
        this.contactNumber = Objects.requireNonNull(contactNumber);
        this.address = Objects.requireNonNull(address);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID getPatientId() { return patientId; }
    public String getFullName() { return fullName; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateContact(String number, String newAddress) {
        this.contactNumber = Objects.requireNonNull(number);
        this.address = Objects.requireNonNull(newAddress);
    }
}
