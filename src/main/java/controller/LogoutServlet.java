package controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cierra la sesión del usuario actual y lo devuelve al login.
 */
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Invalida la sesión activa (si existe) y redirige al login.
     *
     * @param request  petición HTTP
     * @param response respuesta HTTP usada para redirigir
     * @throws IOException si falla el redirect
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/");
    }
}