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

class PrestamoServletTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void unGetSinSesionActivaRedirigeAlLogin() throws Exception {
        PrestamoServlet servlet = new PrestamoServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    void conSesionActivaMuestraElHistorialDePrestamos() throws Exception {
        PrestamoServlet servlet = new PrestamoServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        javax.servlet.RequestDispatcher dispatcher = mock(javax.servlet.RequestDispatcher.class);

        Usuario usuario = new Usuario(1, "Demo", "demo@untec.cl", "1234");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(request.getRequestDispatcher("/WEB-INF/views/prestamos.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void unPostSinSesionActivaRedirigeAlLogin() throws Exception {
        PrestamoServlet servlet = new PrestamoServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    void registrarUnPrestamoPorPostRedirigeAlCatalogo() throws Exception {
        // Prepara un libro real en la base de test, para que el DAO tenga
        // stock disponible y el préstamo se pueda registrar de verdad.
        dao.LibroDAO libroDAO = new dao.LibroDAO();
        libroDAO.agregar(new model.Libro(0, "978-0000000002", "Libro Prestamo Servlet", "Autor", 1, 1));
        model.Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-0000000002"))
                .findFirst()
                .orElseThrow();

        PrestamoServlet servlet = new PrestamoServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Usuario usuario = new Usuario(1, "Demo", "demo@untec.cl", "1234");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(request.getParameter("libroId")).thenReturn(String.valueOf(libro.getId()));
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/libros");
    }
}