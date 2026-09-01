package controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.Usuario;

class DevolucionServletTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void sinSesionActivaRedirigeAlLogin() throws Exception {
        DevolucionServlet servlet = new DevolucionServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    void conSesionActivaRedirigeAMisPrestamosDespuesDeDevolver() throws Exception {
        DevolucionServlet servlet = new DevolucionServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getParameter("prestamoId")).thenReturn("999999");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/prestamos");
    }

    @Test
    void devolverUnPrestamoRealRedirigeAMisPrestamosConMensajeDeExito() throws Exception {
        // Se arma un préstamo real en la base de test para que la
        // devolución tenga un caso legítimo que procesar.
        dao.LibroDAO libroDAO = new dao.LibroDAO();
        dao.PrestamoDAO prestamoDAO = new dao.PrestamoDAO();

        libroDAO.agregar(new model.Libro(0, "978-0000000003", "Libro Devolucion Servlet", "Autor", 1, 1));
        model.Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-0000000003"))
                .findFirst()
                .orElseThrow();

        long idPrestamo = prestamoDAO.registrarPrestamo(1L, libro.getId());

        DevolucionServlet servlet = new DevolucionServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getParameter("prestamoId")).thenReturn(String.valueOf(idPrestamo));
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(session).setAttribute("mensaje", "Libro devuelto correctamente.");
        verify(response).sendRedirect("/prestamos");
    }
}