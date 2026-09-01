package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.LibroDAO;
import model.Libro;

/**
 * Muestra el catálogo (GET) y gestiona alta/eliminación de libros (POST).
 * Solo un usuario con sesión activa puede acceder.
 */
public class LibroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final LibroDAO libroDAO = new LibroDAO();

    /**
     * Muestra el catálogo completo de libros. Requiere sesión activa;
     * si no la hay, redirige al login.
     *
     * @param request  petición HTTP
     * @param response respuesta HTTP usada para redirigir o reenviar a la vista
     * @throws ServletException si falla la consulta del catálogo
     * @throws IOException      si falla el redirect o el forward
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!haySesionActiva(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            request.setAttribute("libros", libroDAO.listarTodos());
            request.getRequestDispatcher("/WEB-INF/views/libros.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("No fue posible cargar el catálogo.", e);
        }
    }

    /**
     * Procesa el alta o eliminación (baja lógica) de un libro, según el
     * parámetro {@code accion} ("agregar" o "eliminar"). Requiere sesión
     * activa; si no la hay, redirige al login. Cualquier otro valor de
     * {@code accion} no modifica nada y solo redirige al catálogo.
     *
     * @param request  petición HTTP con los datos del libro o el id a eliminar
     * @param response respuesta HTTP usada para redirigir
     * @throws ServletException si falla la operación sobre el libro
     * @throws IOException      si falla el redirect
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!haySesionActiva(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("agregar".equals(accion)) {
                agregarLibro(request);
            } else if ("eliminar".equals(accion)) {
                long id = Long.parseLong(request.getParameter("id"));
                libroDAO.eliminar(id);
            }

            response.sendRedirect(request.getContextPath() + "/libros");

        } catch (SQLException e) {
            throw new ServletException("No fue posible completar la operación sobre el libro.", e);
        }
    }

    /**
     * Arma un {@link Libro} nuevo a partir de los parámetros del formulario
     * y lo guarda en el catálogo.
     *
     * @param request petición HTTP con los campos isbn, titulo, autor y ejemplares
     * @throws SQLException si falla la inserción
     */
    private void agregarLibro(HttpServletRequest request) throws SQLException {
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        int ejemplares = Integer.parseInt(request.getParameter("ejemplares"));

        Libro libro = new Libro(0, isbn, titulo, autor, ejemplares, ejemplares);
        libroDAO.agregar(libro);
    }

    /**
     * Verifica si la petición trae una sesión activa con un usuario logueado.
     *
     * @param request petición HTTP a verificar
     * @return {@code true} si hay sesión y usuario logueado, {@code false} en caso contrario
     */
    private boolean haySesionActiva(HttpServletRequest request) {
        return request.getSession(false) != null
                && request.getSession(false).getAttribute("usuarioActual") != null;
    }
}