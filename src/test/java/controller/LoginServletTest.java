package controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoginServletTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void unLoginCorrectoRedirigeAlCatalogo() throws Exception {
        LoginServlet servlet = new LoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("correo")).thenReturn("alumno@untec.cl");
        when(request.getParameter("contrasena")).thenReturn("1234");
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/libros");
    }

    @Test
    void unLoginConCamposVaciosVuelveAlFormulario() throws Exception {
        LoginServlet servlet = new LoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("correo")).thenReturn("");
        when(request.getParameter("contrasena")).thenReturn("");
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), anyString());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void unLoginConClaveIncorrectaVuelveAlFormularioConError() throws Exception {
        LoginServlet servlet = new LoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("correo")).thenReturn("alumno@untec.cl");
        when(request.getParameter("contrasena")).thenReturn("clave-mala");
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), anyString());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void unGetRedirigeSiempreAlFormularioDeLogin() throws Exception {
        LoginServlet servlet = new LoginServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/index.jsp");
    }
}