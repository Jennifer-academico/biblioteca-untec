package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.PrestamoDAO;
import model.Usuario;

/**
 * Lista los préstamos del usuario logueado (GET) y registra un préstamo
 * nuevo cuando pide un libro desde el catálogo (POST).
 */
public class PrestamoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Muestra el historial de préstamos del usuario logueado. Requiere
     * sesión activa; si no la hay, redirige al login.
     *
     * @param request  petición HTTP
     * @param response respuesta HTTP usada para redirigir o reenviar a la vista
     * @throws ServletException si falla la consulta de préstamos
     * @throws IOException      si falla el redirect o el forward
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = usuarioDeLaSesion(request);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            request.setAttribute("prestamos", prestamoDAO.listarPorUsuario(usuario.getId()));
            request.getRequestDispatcher("/WEB-INF/views/prestamos.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("No fue posible consultar los préstamos.", e);
        }
    }

    /**
     * Registra un préstamo nuevo para el libro indicado. Requiere sesión
     * activa; si no la hay, redirige al login. Si no queda stock disponible,
     * el préstamo no se crea y se informa mediante un mensaje en sesión.
     *
     * @param request  petición HTTP con el parámetro {@code libroId}
     * @param response respuesta HTTP usada para redirigir
     * @throws ServletException si falla el registro del préstamo
     * @throws IOException      si falla el redirect
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = usuarioDeLaSesion(request);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            long libroId = Long.parseLong(request.getParameter("libroId"));
            long idPrestamo = prestamoDAO.registrarPrestamo(usuario.getId(), libroId);

            String mensaje = idPrestamo == 0
                    ? "Ese libro ya no tiene ejemplares disponibles."
                    : "Préstamo registrado correctamente.";

            request.getSession().setAttribute("mensaje", mensaje);
            response.sendRedirect(request.getContextPath() + "/libros");

        } catch (SQLException e) {
            throw new ServletException("No fue posible registrar el préstamo.", e);
        }
    }

    /**
     * Obtiene el usuario logueado desde la sesión actual, si existe.
     *
     * @param request petición HTTP a inspeccionar
     * @return el usuario en sesión, o {@code null} si no hay sesión activa
     */
    private Usuario usuarioDeLaSesion(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        return (Usuario) request.getSession(false).getAttribute("usuarioActual");
    }
}