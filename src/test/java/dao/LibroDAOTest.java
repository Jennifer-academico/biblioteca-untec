package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.Libro;

/**
 * Usa la misma base H2 en memoria que UsuarioDAOTest para no tocar
 * los datos reales de la aplicación.
 */
class LibroDAOTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void agregarLibroQuedaDisponible() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        Libro libro = new Libro(0, "978-9999999999", "Libro de Prueba", "Autor de Prueba", 5, 5);
        libroDAO.agregar(libro);

        Libro encontrado = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-9999999999"))
                .findFirst()
                .orElseThrow();

        assertEquals(5, encontrado.getEjemplaresDisponibles());
        assertTrue(encontrado.isDisponible());
        assertTrue(encontrado.isActivo());
    }

    @Test
    void reducirDisponibilidadBajaElStock() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        Libro libro = new Libro(0, "978-8888888888", "Otro Libro", "Otro Autor", 1, 1);
        libroDAO.agregar(libro);

        Libro creado = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-8888888888"))
                .findFirst()
                .orElseThrow();

        boolean resultado = libroDAO.reducirDisponibilidad(creado.getId());
        Libro actualizado = libroDAO.buscarPorId(creado.getId());

        assertTrue(resultado);
        assertEquals(0, actualizado.getEjemplaresDisponibles());
        assertFalse(actualizado.isDisponible());
    }

    @Test
    void noReduceStockSiYaEstaEnCero() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        Libro libro = new Libro(0, "978-7777777777", "Libro Sin Stock", "Autor", 1, 0);
        libroDAO.agregar(libro);

        Libro creado = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-7777777777"))
                .findFirst()
                .orElseThrow();

        libroDAO.reducirDisponibilidad(creado.getId());
        boolean segundaReduccion = libroDAO.reducirDisponibilidad(creado.getId());

        assertFalse(segundaReduccion);
    }

    @Test
    void buscarPorIdInexistenteDevuelveNull() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        Libro resultado = libroDAO.buscarPorId(999999L);

        assertNull(resultado);
    }

    @Test
    void eliminarMarcaInactivoSinBorrar() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        libroDAO.agregar(new Libro(0, "978-6666666666", "Libro A Eliminar", "Autor", 1, 1));
        Libro creado = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-6666666666"))
                .findFirst()
                .orElseThrow();

        libroDAO.eliminar(creado.getId());
        Libro buscado = libroDAO.buscarPorId(creado.getId());

        assertFalse(buscado.isActivo());
    }

    @Test
    void eliminadoNoApareceEnCatalogo() throws Exception {
        LibroDAO libroDAO = new LibroDAO();

        libroDAO.agregar(new Libro(0, "978-5555555555", "Libro A Ocultar", "Autor", 1, 1));
        Libro creado = libroDAO.listarTodos().stream()
                .filter(l -> l.getIsbn().equals("978-5555555555"))
                .findFirst()
                .orElseThrow();

        libroDAO.eliminar(creado.getId());

        boolean sigueEnCatalogo = libroDAO.listarTodos().stream()
                .anyMatch(l -> l.getIsbn().equals("978-5555555555"));

        assertFalse(sigueEnCatalogo);
    }
}