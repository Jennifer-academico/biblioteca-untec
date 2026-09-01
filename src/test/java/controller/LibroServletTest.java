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

class LibroServletTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void sinSesionActivaRedirigeAlLogin() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    void conSesionActivaMuestraElCatalogo() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        javax.servlet.RequestDispatcher dispatcher = mock(javax.servlet.RequestDispatcher.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getRequestDispatcher("/WEB-INF/views/libros.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void agregarUnLibroPorPostRedirigeAlCatalogo() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getParameter("accion")).thenReturn("agregar");
        when(request.getParameter("isbn")).thenReturn("978-0000000001");
        when(request.getParameter("titulo")).thenReturn("Libro de Test Servlet");
        when(request.getParameter("autor")).thenReturn("Autor Test");
        when(request.getParameter("ejemplares")).thenReturn("2");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/libros");
    }

    @Test
    void eliminarUnLibroPorPostRedirigeAlCatalogo() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getParameter("accion")).thenReturn("eliminar");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/libros");
    }

    @Test
    void unPostSinSesionActivaRedirigeAlLogin() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    void unPostConAccionDesconocidaNoModificaNadaYRedirigeIgual() throws Exception {
        LibroServlet servlet = new LibroServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioActual")).thenReturn(new Usuario(1, "Demo", "demo@untec.cl", "1234"));
        when(request.getParameter("accion")).thenReturn("accion-que-no-existe");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        // El servlet no reconoce la acción, no hace nada con la base de datos,
        // pero igual redirige al catálogo (no lanza error ni se cuelga).
        verify(response).sendRedirect("/libros");
    }
}