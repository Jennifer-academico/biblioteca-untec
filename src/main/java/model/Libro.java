package model;

import java.io.Serializable;

/**
 * Representa un libro del catálogo de la biblioteca.
 * Guarda cuántos ejemplares existen en total y cuántos quedan disponibles.
 */
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String isbn;
    private String titulo;
    private String autor;
    private int ejemplaresTotales;
    private int ejemplaresDisponibles;
    private boolean activo;

    public Libro() {
    }

    public Libro(long id, String isbn, String titulo, String autor,
                 int ejemplaresTotales, int ejemplaresDisponibles) {
        this(id, isbn, titulo, autor, ejemplaresTotales, ejemplaresDisponibles, true);
    }

    public Libro(long id, String isbn, String titulo, String autor,
                 int ejemplaresTotales, int ejemplaresDisponibles, boolean activo) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.ejemplaresTotales = ejemplaresTotales;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.activo = activo;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getEjemplaresTotales() { return ejemplaresTotales; }
    public void setEjemplaresTotales(int ejemplaresTotales) { this.ejemplaresTotales = ejemplaresTotales; }

    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /**
     * Un libro puede prestarse solo si queda al menos un ejemplar libre.
     */
    public boolean isDisponible() {
        return ejemplaresDisponibles > 0;
    }
}