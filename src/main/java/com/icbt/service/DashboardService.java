package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReferenceDataDAO;
import com.icbt.model.DashboardStats;

import java.time.LocalDate;

public final class DashboardService {
    private final AppointmentDAO appointmentDAO;
    private final ReferenceDataDAO referenceDataDAO;

    public DashboardService() {
        this(new AppointmentDAO(), new ReferenceDataDAO());
    }

    DashboardService(AppointmentDAO appointmentDAO, ReferenceDataDAO referenceDataDAO) {
        this.appointmentDAO = appointmentDAO;
        this.referenceDataDAO = referenceDataDAO;
    }

    public DashboardStats loadStats(LocalDate today) {
        int todayCount       = appointmentDAO.countForDate(today);
        int dentistCount     = referenceDataDAO.countActiveDentists();
        int completedCount   = appointmentDAO.countCompletedThisMonth(today);
        return new DashboardStats(todayCount, dentistCount, completedCount);
    }
}
