package com.icbt.controller;

import com.icbt.service.BusinessRuleException;
import com.icbt.service.CatalogueService;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CatalogueControllerTest {
    @Test
    public void missingActiveFeeRendersCatalogueWithActionableError() throws Exception {
        CatalogueService catalogueService = mock(CatalogueService.class);
        CatalogueController controller = new CatalogueController(catalogueService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(catalogueService.treatments(any())).thenReturn(List.of());
        when(catalogueService.activeFee(any())).thenThrow(
                new BusinessRuleException("No active consultation fee is configured."));
        when(request.getRequestDispatcher("/WEB-INF/view/catalogue.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(request).setAttribute("catalogueError", "No active consultation fee is configured.");
        verify(dispatcher).forward(request, response);
    }
}
