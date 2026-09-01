package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import model.Usuario;

/**
 * Usa una base H2 en memoria, separada de la base real, para no
 * ensuciar los datos que se ven al probar la aplicación en el navegador.
 */
class UsuarioDAOTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void elUsuarioDemoPuedeAutenticarseConLaClaveCorrecta() throws Exception {
        Usuario usuario = new UsuarioDAO().buscarPorCredenciales("alumno@untec.cl", "1234");

        assertNotNull(usuario);
        assertEquals("Estudiante Demo", usuario.getNombre());
    }

    @Test
    void unaClaveIncorrectaNoDebeAutenticar() throws Exception {
        Usuario usuario = new UsuarioDAO().buscarPorCredenciales("alumno@untec.cl", "clave-mala");

        assertNull(usuario);
    }
}