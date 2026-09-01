package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.PrestamoDAO;

/**
 * Marca un préstamo como devuelto y repone el ejemplar en el catálogo.
 */
public class DevolucionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Procesa la devolución de un préstamo. Requiere sesión activa;
     * si no la hay, redirige al login. Al finalizar, siempre redirige
     * a "Mis préstamos" con un mensaje de éxito o error en la sesión.
     *
     * @param request  petición HTTP, debe traer el parámetro {@code prestamoId}
     * @param response respuesta HTTP usada para redirigir
     * @throws ServletException si falla el registro de la devolución
     * @throws IOException      si falla el redirect
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getSession(false) == null
                || request.getSession(false).getAttribute("usuarioActual") == null) {

            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            long prestamoId = Long.parseLong(request.getParameter("prestamoId"));
            boolean exito = prestamoDAO.registrarDevolucion(prestamoId);

            String mensaje = exito
                    ? "Libro devuelto correctamente."
                    : "Ese préstamo ya estaba devuelto o no existe.";

            request.getSession().setAttribute("mensaje", mensaje);
            response.sendRedirect(request.getContextPath() + "/prestamos");

        } catch (SQLException e) {
            throw new ServletException("No fue posible registrar la devolución.", e);
        }
    }
}