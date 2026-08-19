package com.icbt.service;

import com.icbt.dao.AppointmentDAO;
import com.icbt.dao.ReportDAO;
import com.icbt.model.Appointment;
import com.icbt.model.BillingSummaryRow;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class ReportService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    public List<Appointment> dailyAppointments(StaffUser user, LocalDate date) {
        requireManager(user);
        if (date == null) {
            throw new BusinessRuleException("Choose a report date.");
        }
        return appointmentDAO.findForDate(date);
    }

    public List<BillingSummaryRow> billingSummary(StaffUser user, LocalDate from, LocalDate to) {
        requireManager(user);
        if (from == null || to == null || from.isAfter(to)) {
            throw new BusinessRuleException("Choose a valid report date range.");
        }
        if (ChronoUnit.DAYS.between(from, to) > 366) {
            throw new BusinessRuleException("Report range must not exceed 366 days.");
        }
        return reportDAO.billingSummary(from, to);
    }

    private void requireManager(StaffUser user) {
        if (user == null || !user.hasRole(StaffRole.CLINIC_MANAGER)) {
            throw new SecurityException("Only the clinic manager can generate operational reports.");
        }
    }
}
