package com.icbt.controller;

import com.icbt.model.Appointment;
import com.icbt.model.AppointmentStatus;
import com.icbt.model.Dentist;
import com.icbt.model.Patient;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import com.icbt.model.TreatmentType;
import com.icbt.service.AppointmentService;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppointmentApiContractTest {
    private AppointmentService appointmentService;
    private AppointmentApiController controller;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseBody;

    @Before
    public void setUp() throws Exception {
        appointmentService = mock(AppointmentService.class);
        controller = new AppointmentApiController();
        injectAppointmentService(controller, appointmentService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody, true));
    }

    @Test
    public void servletUsesDocumentedAppointmentApiPath() {
        WebServlet mapping = AppointmentApiController.class.getAnnotation(WebServlet.class);

        assertArrayEquals(new String[]{"/api/appointments"}, mapping.value());
    }

    @Test
    public void knownAppointmentProducesUtf8JsonResponse() throws Exception {
        when(request.getParameter("number")).thenReturn("APT-000001");
        when(appointmentService.findByNumber("APT-000001")).thenReturn(appointment());

        controller.doGet(request, response);

        verify(response).setContentType("application/json;charset=UTF-8");
        String json = responseBody.toString();
        assertTrue(json.contains("\"appointmentNumber\":\"APT-000001\""));
        assertTrue(json.contains("\"patientName\":\"Nimal Perera\""));
        assertTrue(json.contains("\"dentistName\":\"Dr Silva\""));
        assertTrue(json.contains("\"status\":\"SCHEDULED\""));
    }

    @Test
    public void unknownAppointmentProduces404JsonError() throws Exception {
        when(request.getParameter("number")).thenReturn("APT-999999");
        when(appointmentService.findByNumber("APT-999999"))
                .thenThrow(new NotFoundException("Appointment not found."));

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertEquals("{\"error\":\"Appointment not found.\"}", responseBody.toString());
    }

    @Test
    public void blankAppointmentNumberProduces400JsonError() throws Exception {
        when(request.getParameter("number")).thenReturn(" ");
        when(appointmentService.findByNumber(" "))
                .thenThrow(new BusinessRuleException("Enter an appointment number."));

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("{\"error\":\"Enter an appointment number.\"}", responseBody.toString());
    }

    private void injectAppointmentService(
            AppointmentApiController target, AppointmentService service) throws Exception {
        Field field = AppointmentApiController.class.getDeclaredField("appointmentService");
        field.setAccessible(true);
        field.set(target, service);
    }

    private Appointment appointment() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 8, 1, 9, 0);
        StaffUser dentistUser = new StaffUser(
                UUID.randomUUID(), "dr.silva", "not-used-by-this-test", "Dr Silva",
                StaffRole.DENTIST, true, createdAt);
        Dentist dentist = new Dentist(UUID.randomUUID(), dentistUser, true);
        StaffUser receptionist = new StaffUser(
                UUID.randomUUID(), "reception", "not-used-by-this-test", "Receptionist",
                StaffRole.RECEPTIONIST, true, createdAt);
        Patient patient = new Patient(
                UUID.randomUUID(), "Nimal Perera", "0771234567", "10 Galle Road", createdAt);
        TreatmentType treatment = new TreatmentType(
                UUID.randomUUID(), "Filling", new BigDecimal("4500.00"), true);
        return new Appointment(
                UUID.randomUUID(), "APT-000001", patient, dentist, treatment, receptionist,
                LocalDate.of(2026, 8, 20), LocalTime.of(10, 0),
                AppointmentStatus.SCHEDULED, createdAt, null);
    }
}
