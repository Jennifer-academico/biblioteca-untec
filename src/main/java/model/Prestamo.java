package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Une un usuario con un libro que pidió prestado, y guarda las fechas
 * relevantes del préstamo y de la devolución (si ya ocurrió).
 */
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private long id;
    private long usuarioId;
    private long libroId;
    private String tituloLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo() {
    }

    public Prestamo(long id, long usuarioId, long libroId, String tituloLibro,
                     LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
        this.tituloLibro = tituloLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public long getLibroId() { return libroId; }
    public void setLibroId(long libroId) { this.libroId = libroId; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }

    /**
     * Formatea la fecha de préstamo como texto legible (dd-MM-yyyy).
     *
     * @return la fecha formateada, o cadena vacía si no hay fecha
     */
    public String getFechaPrestamoTexto() {
        return fechaPrestamo == null ? "" : fechaPrestamo.format(FORMATO_FECHA);
    }

    /**
     * Formatea la fecha de devolución como texto legible (dd-MM-yyyy).
     *
     * @return la fecha formateada, o "Pendiente" si el préstamo aún no se devolvió
     */
    public String getFechaDevolucionTexto() {
        return fechaDevolucion == null ? "Pendiente" : fechaDevolucion.format(FORMATO_FECHA);
    }
}