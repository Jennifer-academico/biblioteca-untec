package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Usuario;

/**
 * Encapsula todo el SQL relacionado con la tabla usuario.
 * El resto de la aplicación no debería escribir SQL directamente:
 * siempre pasa por acá.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario cuyo correo y contraseña coincidan exactamente.
     * Devuelve null si no hay coincidencia (credenciales incorrectas).
     */
    public Usuario buscarPorCredenciales(String correo, String contrasena) throws SQLException {
        String sql = "SELECT id, nombre, correo, contrasena FROM usuario WHERE correo = ? AND contrasena = ?";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    return mapearFila(resultado);
                }
            }
        }

        return null;
    }

    /**
     * Convierte una fila del ResultSet en un objeto {@link Usuario}.
     *
     * @param fila fila actual del ResultSet, ya posicionada con {@code next()}
     * @return el usuario construido a partir de esa fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Usuario mapearFila(ResultSet fila) throws SQLException {
        return new Usuario(
                fila.getLong("id"),
                fila.getString("nombre"),
                fila.getString("correo"),
                fila.getString("contrasena")
        );
    }
}