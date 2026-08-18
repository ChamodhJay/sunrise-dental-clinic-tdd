package com.icbt.service;

import com.icbt.dao.BillDAO;
import com.icbt.model.Appointment;
import com.icbt.model.AppointmentStatus;
import com.icbt.model.Bill;
import com.icbt.model.BillStatus;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;

public final class BillingService {
    private final AppointmentService appointmentService;
    private final BillDAO billDAO;

    public BillingService() {
        this(new AppointmentService(), new BillDAO());
    }

    BillingService(AppointmentService appointmentService, BillDAO billDAO) {
        this.appointmentService = appointmentService;
        this.billDAO = billDAO;
    }

    public Bill calculateBill(String appointmentNumber, StaffUser user) {
        requireReceptionist(user);
        Appointment appointment = appointmentService.findByNumber(appointmentNumber);
        if (appointment.getStatus() != AppointmentStatus.COMPLETED
                || appointment.getTreatmentRecord() == null) {
            throw new BusinessRuleException("A bill can be generated only after the dentist records treatment.");
        }
        Bill existing = billDAO.findByAppointment(appointment).orElse(null);
        if (existing != null) {
            return existing;
        }
        return billDAO.createForCompletedAppointment(appointment, user);
    }

    public Bill findBill(String appointmentNumber) {
        Appointment appointment = appointmentService.findByNumber(appointmentNumber);
        return billDAO.findByAppointment(appointment)
                .orElseThrow(() -> new NotFoundException("No bill exists for this appointment."));
    }

    public Bill markPrinted(String appointmentNumber, StaffUser user) {
        requireReceptionist(user);
        Bill bill = findBill(appointmentNumber);
        billDAO.updateStatus(bill.getBillId(), BillStatus.PRINTED);
        bill.markPrinted();
        return bill;
    }

    private void requireReceptionist(StaffUser user) {
        if (user == null || !user.hasRole(StaffRole.RECEPTIONIST)) {
            throw new SecurityException("Only a receptionist can generate or print patient bills.");
        }
    }
}
