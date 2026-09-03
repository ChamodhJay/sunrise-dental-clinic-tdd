package com.icbt.filter;

import com.icbt.controller.WebSupport;
import com.icbt.dao.StaffUserDAO;
import com.icbt.model.StaffRole;
import com.icbt.model.StaffUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecurityFilterTest {
    private StaffUserDAO staffUserDAO;
    private SecurityFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @Before
    public void setUp() {
        staffUserDAO = mock(StaffUserDAO.class);
        filter = new SecurityFilter(staffUserDAO);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/sunrise-dental-clinic");
    }

    @Test
    public void publicAssetsDoNotCreateServerSessions() throws Exception {
        when(request.getServletPath()).thenReturn("/assets/app.css");

        filter.doFilter(request, response, chain);

        verify(request).getSession(false);
        verify(request, never()).getSession(true);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void contextRootRedirectsToLoginInsteadOfFallingThroughToMissingWelcomePage() throws Exception {
        when(request.getServletPath()).thenReturn("");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/sunrise-dental-clinic/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void traceRequestsAreRejectedBeforeApplicationDispatch() throws Exception {
        when(request.getMethod()).thenReturn("TRACE");

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void unsafeProtectedRequestRequiresCsrfAndStoresNoPasswordHash() throws Exception {
        StaffUser user = user();
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getServletPath()).thenReturn("/appointments");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(WebSupport.AUTH_USER)).thenReturn(user);
        when(session.getAttribute(WebSupport.CSRF_TOKEN)).thenReturn("expected-token");
        when(staffUserDAO.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(request.getRequestDispatcher("/WEB-INF/view/error.jsp")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
        ArgumentCaptor<Object> principal = ArgumentCaptor.forClass(Object.class);
        verify(session).setAttribute(eq(WebSupport.AUTH_USER), principal.capture());
        assertEquals("", ((StaffUser) principal.getValue()).getPasswordHash());
    }

    @Test
    public void malformedCsrfSessionAttributeIsRejectedWithoutServerError() throws Exception {
        StaffUser user = user();
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/appointments");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(WebSupport.AUTH_USER)).thenReturn(user);
        when(session.getAttribute(WebSupport.CSRF_TOKEN)).thenReturn(12345);
        when(staffUserDAO.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(request.getRequestDispatcher("/WEB-INF/view/error.jsp")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    private StaffUser user() {
        return new StaffUser(UUID.randomUUID(), "reception", "sensitive-hash", "Receptionist",
                StaffRole.RECEPTIONIST, true, LocalDateTime.of(2026, 8, 20, 9, 0));
    }
}
