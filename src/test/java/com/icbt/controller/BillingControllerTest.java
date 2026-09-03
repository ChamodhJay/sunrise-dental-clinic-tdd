package com.icbt.controller;

import com.icbt.service.BillingService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BillingControllerTest {
    private BillingService billingService;
    private BillingController controller;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @Before
    public void setUp() {
        billingService = mock(BillingService.class);
        controller = new BillingController(billingService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        when(request.getRequestDispatcher("/WEB-INF/view/billing.jsp")).thenReturn(dispatcher);
    }

    @Test
    public void unknownActionIsRejectedWithoutCreatingABill() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("appointmentNumber")).thenReturn("APT-260903-0001");

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(request).setAttribute("billingError", "Select a valid billing action.");
        verify(dispatcher).forward(request, response);
        verifyNoInteractions(billingService);
    }

    @Test
    public void blankAppointmentNumberIsRejectedBeforeServiceCall() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("appointmentNumber")).thenReturn("  ");

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(request).setAttribute("billingError", "Appointment number is required.");
        verify(dispatcher).forward(request, response);
        verifyNoInteractions(billingService);
    }
}
