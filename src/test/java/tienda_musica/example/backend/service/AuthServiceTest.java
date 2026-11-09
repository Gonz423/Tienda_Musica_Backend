package tienda_musica.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tienda_musica.example.backend.dto.LoginRequest;
import tienda_musica.example.backend.dto.LoginResponse;
import tienda_musica.example.backend.dto.RegistroRequest;
import tienda_musica.example.backend.model.Usuario;
import tienda_musica.example.backend.repository.UsuarioRepository;
import tienda_musica.example.backend.security.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setNombre("Gonzalo Test");
        usuarioPrueba.setCorreo("gonzalo@tiendamusica.cl");
        usuarioPrueba.setPassword("$2a$10$hashedpassword");
        usuarioPrueba.setRol("ADMIN");
        usuarioPrueba.setActivo(true);
    }

    @Test
    @DisplayName("Login exitoso devuelve token JWT y datos de usuario")
    void testLoginExitoso() {
        when(usuarioRepository.findByCorreoIgnoreCase("gonzalo@tiendamusica.cl"))
                .thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("Admin123!", usuarioPrueba.getPassword())).thenReturn(true);
        when(jwtService.generarToken("gonzalo@tiendamusica.cl", "ADMIN")).thenReturn("mocked.jwt.token");

        LoginResponse response = authService.login(new LoginRequest("gonzalo@tiendamusica.cl", "Admin123!"));

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("gonzalo@tiendamusica.cl", response.correo());
        assertEquals("ADMIN", response.rol());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza BadCredentialsException")
    void testLoginPasswordIncorrecto() {
        when(usuarioRepository.findByCorreoIgnoreCase("gonzalo@tiendamusica.cl"))
                .thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("PasswordErronea", usuarioPrueba.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                authService.login(new LoginRequest("gonzalo@tiendamusica.cl", "PasswordErronea")));
    }

    @Test
    @DisplayName("Registro exitoso de nuevo cliente devuelve token JWT")
    void testRegistroExitoso() {
        when(usuarioRepository.existsByCorreoIgnoreCase("nuevo@cliente.cl")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("$2a$10$encodedpassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtService.generarToken("nuevo@cliente.cl", "CLIENTE")).thenReturn("new.client.jwt.token");

        RegistroRequest request = new RegistroRequest(
                "Nuevo Cliente",
                "nuevo@cliente.cl",
                "Secret123!",
                "12.345.678-9",
                "+56911223344",
                "Calle 123"
        );

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("new.client.jwt.token", response.token());
        assertEquals("nuevo@cliente.cl", response.correo());
        assertEquals("CLIENTE", response.rol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registro con correo duplicado lanza IllegalArgumentException")
    void testRegistroCorreoDuplicado() {
        when(usuarioRepository.existsByCorreoIgnoreCase("gonzalo@tiendamusica.cl")).thenReturn(true);

        RegistroRequest request = new RegistroRequest(
                "Duplicado",
                "gonzalo@tiendamusica.cl",
                "Password123!",
                null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
