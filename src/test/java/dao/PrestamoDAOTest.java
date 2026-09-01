package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.Libro;

/**
 * Usa la misma base H2 en memoria que los demás DAO test, para no tocar
 * los datos reales de la aplicación.
 */
class PrestamoDAOTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void registrarUnPrestamoDescuentaUnEjemplarDisponible() throws Exception {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        libroDAO.agregar(new Libro(0, "978-1111111111", "Libro Prestable", "Autor", 1, 1));
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-1111111111"))
                .findFirst()
                .orElseThrow();

        long idPrestamo = prestamoDAO.registrarPrestamo(1L, libro.getId());
        Libro libroActualizado = libroDAO.buscarPorId(libro.getId());

        assertNotEquals(0L, idPrestamo);
        assertEquals(0, libroActualizado.getEjemplaresDisponibles());
    }

    @Test
    void noSePuedePrestarUnLibroSinStock() throws Exception {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        libroDAO.agregar(new Libro(0, "978-2222222222", "Libro Sin Copias", "Autor", 1, 1));
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-2222222222"))
                .findFirst()
                .orElseThrow();

        // El primer préstamo consume el único ejemplar disponible.
        prestamoDAO.registrarPrestamo(1L, libro.getId());

        // El segundo debe fallar porque ya no hay stock.
        long segundoIntento = prestamoDAO.registrarPrestamo(1L, libro.getId());

        assertEquals(0L, segundoIntento);
    }

    @Test
    void devolverUnPrestamoRestituyeElEjemplar() throws Exception {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        libroDAO.agregar(new Libro(0, "978-3333333333", "Libro Con Devolucion", "Autor", 1, 1));
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-3333333333"))
                .findFirst()
                .orElseThrow();

        long idPrestamo = prestamoDAO.registrarPrestamo(1L, libro.getId());
        boolean devuelto = prestamoDAO.registrarDevolucion(idPrestamo);
        Libro libroFinal = libroDAO.buscarPorId(libro.getId());

        assertTrue(devuelto);
        assertEquals(1, libroFinal.getEjemplaresDisponibles());
    }

    @Test
    void noSePuedeDevolverDosVecesElMismoPrestamo() throws Exception {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();

        libroDAO.agregar(new Libro(0, "978-4444444444", "Libro Doble Devolucion", "Autor", 1, 1));
        Libro libro = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-4444444444"))
                .findFirst()
                .orElseThrow();

        long idPrestamo = prestamoDAO.registrarPrestamo(1L, libro.getId());
        prestamoDAO.registrarDevolucion(idPrestamo);
        boolean segundaDevolucion = prestamoDAO.registrarDevolucion(idPrestamo);

        assertFalse(segundaDevolucion);
    }
}