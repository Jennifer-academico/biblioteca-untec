package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UsuarioDAO;
import model.Usuario;

/**
 * Procesa el formulario de login. Si las credenciales son correctas,
 * guarda el usuario en la sesión y redirige al catálogo.
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida el correo y la contraseña recibidos del formulario de login.
     * Si son correctos, abre sesión y redirige a "/libros"; si no,
     * vuelve al formulario mostrando un mensaje de error.
     *
     * @param request  petición HTTP con los parámetros correo y contrasena
     * @param response respuesta HTTP usada para redirigir
     * @throws ServletException si falla la validación de credenciales
     * @throws IOException      si falla el redirect o el forward
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            request.setAttribute("error", "Debes ingresar correo y contraseña.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorCredenciales(correo.trim(), contrasena);

            if (usuario == null) {
                request.setAttribute("error", "Correo o contraseña incorrectos.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuarioActual", usuario);

            response.sendRedirect(request.getContextPath() + "/libros");

        } catch (SQLException e) {
            throw new ServletException("No fue posible validar las credenciales.", e);
        }
    }

    /**
     * Redirige cualquier acceso por GET de vuelta al formulario de login.
     *
     * @param request  petición HTTP
     * @param response respuesta HTTP usada para redirigir
     * @throws ServletException no aplica en este método
     * @throws IOException      si falla el redirect
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}