package dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConexionBDTest {

    @BeforeAll
    static void configurarBaseDeTest() {
        System.setProperty(
                "biblioteca.db.url",
                "jdbc:h2:mem:biblioteca_untec_test;MODE=MySQL;DB_CLOSE_DELAY=-1");
    }

    @Test
    void getInstanciaSiempreDevuelveLaMismaInstancia() {
        ConexionBD primeraLlamada = ConexionBD.getInstancia();
        ConexionBD segundaLlamada = ConexionBD.getInstancia();

        assertSame(primeraLlamada, segundaLlamada);
    }

    @Test
    void getConexionDevuelveUnaConexionValida() throws Exception {
        var conexion = ConexionBD.getInstancia().getConexion();

        assertNotNull(conexion);
    }
}