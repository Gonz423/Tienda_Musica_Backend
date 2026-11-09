package tienda_musica.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "iuwyeriuwyriweiw123456789012345678901234567890!");
        ReflectionTestUtils.setField(jwtService, "issuer", "tienda-musica-auth");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    @DisplayName("Genera token JWT válido con claims de correo y rol")
    void testGenerarYValidarToken() {
        String correo = "gonzalo@tiendamusica.cl";
        String rol = "ADMIN";

        String token = jwtService.generarToken(correo, rol);

        assertNotNull(token);
        assertTrue(token.length() > 20);

        assertTrue(jwtService.esTokenValido(token));
        assertEquals(correo, jwtService.obtenerCorreo(token));
        assertEquals(rol, jwtService.obtenerRol(token));
    }

    @Test
    @DisplayName("Detecta token inválido o corrupto")
    void testTokenInvalido() {
        assertFalse(jwtService.esTokenValido("token.totalmente.invalido"));
        assertFalse(jwtService.esTokenValido(null));
        assertFalse(jwtService.esTokenValido(""));
    }
}
