package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Libro;

/**
 * CRUD del catálogo de libros y control del stock disponible.
 */
public class LibroDAO {

    /**
     * Obtiene los libros activos del catálogo, ordenados por título.
     * Los libros con baja lógica (eliminados) no se incluyen.
     *
     * @return la lista de libros activos; vacía si no hay ninguno cargado
     * @throws SQLException si falla la consulta a la base de datos
     */
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT id, isbn, titulo, autor, ejemplares_totales, ejemplares_disponibles, activo "
                + "FROM libro WHERE activo = TRUE ORDER BY titulo";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                libros.add(mapearFila(resultado));
            }
        }

        return libros;
    }

    /**
     * Busca un libro puntual por su identificador (incluye libros dados de baja).
     *
     * @param id identificador del libro a buscar
     * @return el libro encontrado, o {@code null} si no existe ninguno con ese id
     * @throws SQLException si falla la consulta a la base de datos
     */
    public Libro buscarPorId(long id) throws SQLException {
        String sql = "SELECT id, isbn, titulo, autor, ejemplares_totales, ejemplares_disponibles, activo "
                + "FROM libro WHERE id = ?";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {
                return resultado.next() ? mapearFila(resultado) : null;
            }
        }
    }

    /**
     * Inserta un libro nuevo en el catálogo, activo por defecto. El stock
     * disponible inicial queda igual al total de ejemplares indicado.
     *
     * @param libro datos del libro a crear (el id se ignora, lo genera la base)
     * @throws SQLException si falla la inserción
     */
    public void agregar(Libro libro) throws SQLException {
        String sql = "INSERT INTO libro (isbn, titulo, autor, ejemplares_totales, ejemplares_disponibles, activo) "
                + "VALUES (?, ?, ?, ?, ?, TRUE)";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getIsbn());
            stmt.setString(2, libro.getTitulo());
            stmt.setString(3, libro.getAutor());
            stmt.setInt(4, libro.getEjemplaresTotales());
            stmt.setInt(5, libro.getEjemplaresTotales());
            stmt.executeUpdate();
        }
    }

    /**
     * Da de baja lógica a un libro (lo marca como inactivo) en vez de
     * borrarlo físicamente, para conservar el historial de préstamos
     * asociado sin violar la integridad referencial de la base de datos.
     *
     * @param id identificador del libro a dar de baja
     * @throws SQLException si falla la actualización
     */
    public void eliminar(long id) throws SQLException {
        String sql = "UPDATE libro SET activo = FALSE WHERE id = ?";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Baja un ejemplar disponible. Solo tiene efecto si todavía queda
     * al menos uno (la condición WHERE evita que el contador quede negativo).
     */
    public boolean reducirDisponibilidad(long libroId) throws SQLException {
        String sql = "UPDATE libro SET ejemplares_disponibles = ejemplares_disponibles - 1 "
                + "WHERE id = ? AND ejemplares_disponibles > 0";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, libroId);
            return stmt.executeUpdate() == 1;
        }
    }

    /**
     * Sube un ejemplar disponible al devolver un libro (nunca supera el total).
     */
    public boolean aumentarDisponibilidad(long libroId) throws SQLException {
        String sql = "UPDATE libro SET ejemplares_disponibles = ejemplares_disponibles + 1 "
                + "WHERE id = ? AND ejemplares_disponibles < ejemplares_totales";

        Connection conexion = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, libroId);
            return stmt.executeUpdate() == 1;
        }
    }

    /**
     * Convierte una fila del ResultSet en un objeto {@link Libro}.
     *
     * @param fila fila actual del ResultSet, ya posicionada con {@code next()}
     * @return el libro construido a partir de esa fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Libro mapearFila(ResultSet fila) throws SQLException {
        return new Libro(
                fila.getLong("id"),
                fila.getString("isbn"),
                fila.getString("titulo"),
                fila.getString("autor"),
                fila.getInt("ejemplares_totales"),
                fila.getInt("ejemplares_disponibles"),
                fila.getBoolean("activo")
        );
    }
}