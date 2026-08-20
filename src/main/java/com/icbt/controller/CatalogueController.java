package com.icbt.controller;

import com.icbt.dao.DataAccessException;
import com.icbt.service.BusinessRuleException;
import com.icbt.service.CatalogueService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/catalogue")
public final class CatalogueController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient CatalogueService catalogueService;

    @Override
    public void init() {
        catalogueService = new CatalogueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if ("fee".equals(action)) {
                catalogueService.changeConsultationFee(WebSupport.user(request),
                        request.getParameter("consultationFee"));
            } else if ("treatment".equals(action)) {
                catalogueService.saveTreatment(WebSupport.user(request), request.getParameter("treatmentId"),
                        request.getParameter("name"), request.getParameter("price"),
                        "true".equals(request.getParameter("active")));
            } else {
                throw new BusinessRuleException("Select a valid catalogue action.");
            }
            response.sendRedirect(request.getContextPath() + "/catalogue?saved=1");
        } catch (BusinessRuleException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("catalogueError", exception.getMessage());
            show(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Catalogue changes could not be saved. The database transaction was rolled back.");
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("treatments", catalogueService.treatments(WebSupport.user(request)));
            request.setAttribute("activeFee", catalogueService.activeFee(WebSupport.user(request)));
            request.getRequestDispatcher("/WEB-INF/view/catalogue.jsp").forward(request, response);
        } catch (DataAccessException exception) {
            WebSupport.error(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Catalogue data is temporarily unavailable.");
        }
    }
}
