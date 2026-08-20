package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.Appointment;
import com.icbt.service.AppointmentService;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;
import com.icbt.service.RegisterAppointmentCommand;
import com.icbt.service.SchedulingConflictException;
import com.icbt.service.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@WebServlet("/appointments")
public final class AppointmentController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient AppointmentService appointmentService;

    @Override
    public void init() {
        appointmentService = new AppointmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("new".equals(action)) {
                showRegistrationForm(request, response);
            } else if ("view".equals(action)) {
                Appointment appointment = appointmentService.findByNumber(request.getParameter("number"));
                request.setAttribute("appointment", appointment);
                request.getRequestDispatcher("/WEB-INF/view/appointment-details.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/view/appointment-search.jsp").forward(request, response);
            }
        } catch (NotFoundException exception) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("searchError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/appointment-search.jsp").forward(request, response);
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("searchError", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/appointment-search.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            databaseError(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RegisterAppointmentCommand command = new RegisterAppointmentCommand(
                request.getParameter("patientName"), request.getParameter("address"),
                request.getParameter("contactNumber"), parseUuid(request.getParameter("dentistId")),
                parseUuid(request.getParameter("treatmentTypeId")),
                parseDate(request.getParameter("appointmentDate")),
                parseTime(request.getParameter("appointmentTime")));
        try {
            Appointment appointment = appointmentService.register(command, WebSupport.user(request));
            String number = URLEncoder.encode(appointment.getAppointmentNumber(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath()
                    + "/appointments?action=view&created=1&number=" + number);
        } catch (ValidationException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("fieldErrors", exception.getFieldErrors());
            showRegistrationForm(request, response);
        } catch (SchedulingConflictException exception) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            request.setAttribute("formError", exception.getMessage());
            showRegistrationForm(request, response);
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("formError", exception.getMessage());
            showRegistrationForm(request, response);
        } catch (SecurityException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Only a receptionist can register appointments.");
        } catch (DataAccessException exception) {
            databaseError(request, response);
        }
    }

    private void showRegistrationForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("dentists", appointmentService.activeDentists());
        request.setAttribute("treatments", appointmentService.activeTreatmentTypes());
        request.setAttribute("minimumDate", LocalDate.now().plusDays(1));
        request.getRequestDispatcher("/WEB-INF/view/appointment-form.jsp").forward(request, response);
    }

    private UUID parseUuid(String value) {
        try {
            return value == null ? null : UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return value == null ? null : LocalTime.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private void databaseError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Appointment data is temporarily unavailable. Check the database connection.");
    }
}
