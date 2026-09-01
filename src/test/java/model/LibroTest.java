package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LibroTest {

    @Test
    void unLibroConEjemplaresDisponiblesDebeEstarDisponible() {
        Libro libro = new Libro(1, "978-1", "Java", "Autor", 3, 1);
        assertTrue(libro.isDisponible());
    }

    @Test
    void unLibroSinEjemplaresDisponiblesNoDebeEstarDisponible() {
        Libro libro = new Libro(1, "978-1", "Java", "Autor", 3, 0);
        assertFalse(libro.isDisponible());
    }
}