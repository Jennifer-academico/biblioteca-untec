package model;

import java.io.Serializable;

/**
 * Representa a un usuario que puede iniciar sesión y pedir libros prestados.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String nombre;
    private String correo;
    private String contrasena;

    public Usuario() {
    }

    public Usuario(long id, String nombre, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}