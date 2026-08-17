package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.Appointment;
import com.icbt.service.AppointmentService;
import com.icbt.service.BusinessRuleException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/dentist/appointments")
public final class DentistController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService;

    @Override
    public void init() {
        appointmentService = new AppointmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Appointment> assigned = appointmentService.findAssignedAppointments(WebSupport.user(request));
            request.setAttribute("appointments", assigned);
            String id = request.getParameter("id");
            if (id != null && !id.isBlank()) {
                Appointment selected = assigned.stream()
                        .filter(item -> item.getAppointmentId().toString().equals(id))
                        .findFirst().orElse(null);
                if (selected == null) {
                    WebSupport.error(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "The requested appointment is not assigned to you.");
                    return;
                }
                request.setAttribute("selectedAppointment", selected);
            }
            request.getRequestDispatcher("/WEB-INF/view/dentist-appointments.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Assigned appointments are temporarily unavailable.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UUID appointmentId = UUID.fromString(request.getParameter("appointmentId"));
            appointmentService.recordTreatment(appointmentId, WebSupport.user(request),
                    request.getParameter("diagnosis"), request.getParameter("treatmentNotes"));
            response.sendRedirect(request.getContextPath() + "/dentist/appointments?recorded=1");
        } catch (IllegalArgumentException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "The appointment identifier is invalid.");
        } catch (BusinessRuleException | IllegalStateException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_CONFLICT, exception.getMessage());
        } catch (SecurityException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Treatment details could not be saved. No partial update was committed.");
        }
    }
}
