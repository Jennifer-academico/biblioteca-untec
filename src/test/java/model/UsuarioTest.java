package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void elConstructorAsignaTodosLosCamposCorrectamente() {
        Usuario usuario = new Usuario(1, "Estudiante Demo", "alumno@untec.cl", "1234");

        assertEquals(1, usuario.getId());
        assertEquals("Estudiante Demo", usuario.getNombre());
        assertEquals("alumno@untec.cl", usuario.getCorreo());
        assertEquals("1234", usuario.getContrasena());
    }

    @Test
    void losSettersActualizanCadaCampo() {
        Usuario usuario = new Usuario();

        usuario.setId(2);
        usuario.setNombre("Otro Nombre");
        usuario.setCorreo("otro@untec.cl");
        usuario.setContrasena("5678");

        assertEquals(2, usuario.getId());
        assertEquals("Otro Nombre", usuario.getNombre());
        assertEquals("otro@untec.cl", usuario.getCorreo());
        assertEquals("5678", usuario.getContrasena());
    }
}