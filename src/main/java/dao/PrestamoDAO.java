package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Prestamo;

/**
 * Registra préstamos y devoluciones, y consulta el historial de un usuario.
 * Antes de crear un préstamo, coordina con LibroDAO para descontar stock.
 */
public class PrestamoDAO {

    private final LibroDAO libroDAO = new LibroDAO();

    /**
     * Lista todos los préstamos de un usuario, del más reciente al más antiguo.
     *
     * @param usuarioId identificador del usuario cuyo historial se consulta
     * @return la lista de préstamos del usuario; vacía si no tiene ninguno
     * @throws SQLException si falla la consulta a la base de datos
     */
    public List<Prestamo> listarPorUsuario(long usuarioId) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();

        String sql = "SELECT p.id, p.usuario_id, p.libro_id, l.titulo, "
                + "p.fecha_prestamo, p.fecha_devolucion, p.devuelto "
                + "FROM prestamo p JOIN libro l ON l.id = p.libro_id "
                + "WHERE p.usuario_id = ? ORDER BY p.id DESC";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);

            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    prestamos.add(mapearFila(resultado));
                }
            }
        }

        return prestamos;
    }

    /**
     * Registra un préstamo nuevo, siempre que quede stock disponible.
     * Devuelve 0 si no había ejemplares libres (no se crea nada en ese caso).
     */
    public long registrarPrestamo(long usuarioId, long libroId) throws SQLException {
        if (!libroDAO.reducirDisponibilidad(libroId)) {
            return 0L;
        }

        String sql = "INSERT INTO prestamo (usuario_id, libro_id, fecha_prestamo, devuelto) "
                + "VALUES (?, ?, ?, FALSE)";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, usuarioId);
            stmt.setLong(2, libroId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                return claves.next() ? claves.getLong(1) : 0L;
            }
        }
    }

    /**
     * Marca un préstamo como devuelto y repone el ejemplar en el catálogo.
     * Devuelve false si el préstamo no existe o ya estaba devuelto.
     */
    public boolean registrarDevolucion(long prestamoId) throws SQLException {
        Connection conexion = ConexionBD.getInstancia().getConexion();

        long libroId;

        String sqlConsulta = "SELECT libro_id FROM prestamo WHERE id = ? AND devuelto = FALSE";
        try (PreparedStatement stmt = conexion.prepareStatement(sqlConsulta)) {
            stmt.setLong(1, prestamoId);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }
                libroId = resultado.getLong("libro_id");
            }
        }

        String sqlActualizar = "UPDATE prestamo SET devuelto = TRUE, fecha_devolucion = ? "
                + "WHERE id = ? AND devuelto = FALSE";

        try (PreparedStatement stmt = conexion.prepareStatement(sqlActualizar)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setLong(2, prestamoId);

            if (stmt.executeUpdate() != 1) {
                return false;
            }
        }

        libroDAO.aumentarDisponibilidad(libroId);
        return true;
    }

    /**
     * Convierte una fila del ResultSet (con el join a libro) en un objeto
     * {@link Prestamo}.
     *
     * @param fila fila actual del ResultSet, ya posicionada con {@code next()}
     * @return el préstamo construido a partir de esa fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Prestamo mapearFila(ResultSet fila) throws SQLException {
        Date fechaDevolucion = fila.getDate("fecha_devolucion");

        return new Prestamo(
                fila.getLong("id"),
                fila.getLong("usuario_id"),
                fila.getLong("libro_id"),
                fila.getString("titulo"),
                fila.getDate("fecha_prestamo").toLocalDate(),
                fechaDevolucion == null ? null : fechaDevolucion.toLocalDate(),
                fila.getBoolean("devuelto")
        );
    }
}