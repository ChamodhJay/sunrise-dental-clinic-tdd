package com.icbt.model;

public final class DashboardStats {
    private final int todayAppointmentCount;
    private final int activeDentistCount;
    private final int completedVisitsThisMonth;

    public DashboardStats(int todayAppointmentCount, int activeDentistCount,
                          int completedVisitsThisMonth) {
        this.todayAppointmentCount = todayAppointmentCount;
        this.activeDentistCount = activeDentistCount;
        this.completedVisitsThisMonth = completedVisitsThisMonth;
    }

    public int getTodayAppointmentCount()      { return todayAppointmentCount; }
    public int getActiveDentistCount()         { return activeDentistCount; }
    public int getCompletedVisitsThisMonth()   { return completedVisitsThisMonth; }
}
