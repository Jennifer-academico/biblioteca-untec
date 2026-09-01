package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PrestamoTest {

    @Test
    void unPrestamoSinFechaDeDevolucionMuestraPendiente() {
        Prestamo prestamo = new Prestamo(
                1, 1, 1, "Un libro",
                LocalDate.of(2026, 8, 1),
                null,
                false);

        assertEquals("Pendiente", prestamo.getFechaDevolucionTexto());
    }

    @Test
    void unPrestamoDevueltoMuestraLaFechaFormateada() {
        Prestamo prestamo = new Prestamo(
                1, 1, 1, "Un libro",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 15),
                true);

        assertEquals("15-08-2026", prestamo.getFechaDevolucionTexto());
    }

    @Test
    void laFechaDePrestamoSeFormateaCorrectamente() {
        Prestamo prestamo = new Prestamo(
                1, 1, 1, "Un libro",
                LocalDate.of(2026, 1, 5),
                null,
                false);

        assertEquals("05-01-2026", prestamo.getFechaPrestamoTexto());
    }
}