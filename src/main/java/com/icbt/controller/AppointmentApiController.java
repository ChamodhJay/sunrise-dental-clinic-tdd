package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.model.Appointment;
import com.icbt.service.AppointmentService;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/appointments")
public final class AppointmentApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient AppointmentService appointmentService;

    @Override
    public void init() {
        appointmentService = new AppointmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Appointment appointment = appointmentService.findByNumber(request.getParameter("number"));
            try (PrintWriter writer = response.getWriter()) {
                writer.print(toJson(appointment));
            }
        } catch (NotFoundException exception) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        } catch (BusinessRuleException exception) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (DataAccessException exception) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Appointment data is temporarily unavailable.");
        }
    }

    private String toJson(Appointment appointment) {
        return "{" +
                "\"appointmentNumber\":" + quote(appointment.getAppointmentNumber()) + "," +
                "\"patientName\":" + quote(appointment.getPatient().getFullName()) + "," +
                "\"address\":" + quote(appointment.getPatient().getAddress()) + "," +
                "\"contactNumber\":" + quote(appointment.getPatient().getContactNumber()) + "," +
                "\"dentistName\":" + quote(appointment.getDentist().getFullName()) + "," +
                "\"treatmentType\":" + quote(appointment.getTreatmentType().getName()) + "," +
                "\"appointmentDate\":" + quote(appointment.getAppointmentDate().toString()) + "," +
                "\"appointmentTime\":" + quote(appointment.getAppointmentTime().toString()) + "," +
                "\"status\":" + quote(appointment.getStatus().name()) +
                "}";
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter()) {
            writer.print("{\"error\":" + quote(message) + "}");
        }
    }

    private String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.append('"').toString();
    }
}
