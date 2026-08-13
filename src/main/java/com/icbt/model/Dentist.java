package com.icbt.model;

import java.util.Objects;
import java.util.UUID;

public final class Dentist {
    private final UUID dentistId;
    private final StaffUser staffUser;
    private final boolean bookingEnabled;

    public Dentist(UUID dentistId, StaffUser staffUser, boolean bookingEnabled) {
        this.dentistId = Objects.requireNonNull(dentistId);
        this.staffUser = Objects.requireNonNull(staffUser);
        this.bookingEnabled = bookingEnabled;
    }

    public UUID getDentistId() { return dentistId; }
    public StaffUser getStaffUser() { return staffUser; }
    public String getFullName() { return staffUser.getFullName(); }
    public boolean isBookingEnabled() { return bookingEnabled; }
    public boolean isActiveForBooking() {
        return bookingEnabled && staffUser.isActive() && staffUser.hasRole(StaffRole.DENTIST);
    }
}
