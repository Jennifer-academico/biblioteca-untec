package controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

class LogoutServletTest {

    @Test
    void cerrarSesionInvalidaLaSesionYRedirigeAlLogin() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/");
    }

    @Test
    void sinSesionActivaSimplementeRedirigeAlLogin() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/");
    }
}