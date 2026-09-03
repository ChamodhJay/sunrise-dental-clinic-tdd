package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.service.DashboardService;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardControllerTest {
    @Test
    public void databaseFailureReturnsControlledServiceUnavailablePage() throws Exception {
        DashboardService dashboardService = mock(DashboardService.class);
        DashboardController controller = new DashboardController(dashboardService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(dashboardService.loadStats(any())).thenThrow(
                new DataAccessException("database offline", new IllegalStateException("offline")));
        when(request.getRequestDispatcher("/WEB-INF/view/error.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        verify(request).setAttribute("errorMessage",
                "Dashboard data is temporarily unavailable. Try again shortly.");
        verify(dispatcher).forward(request, response);
    }
}
