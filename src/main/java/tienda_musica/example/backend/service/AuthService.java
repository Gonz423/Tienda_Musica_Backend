package tienda_musica.example.backend.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.example.backend.dto.LoginRequest;
import tienda_musica.example.backend.dto.LoginResponse;
import tienda_musica.example.backend.dto.RegistroRequest;
import tienda_musica.example.backend.model.Usuario;
import tienda_musica.example.backend.repository.UsuarioRepository;
import tienda_musica.example.backend.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadCredentialsException("La cuenta se encuentra desactivada");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
        return new LoginResponse(token, usuario.getCorreo(), usuario.getNombre(), usuario.getRol());
    }

    @Transactional
    public LoginResponse register(RegistroRequest request) {
        if (usuarioRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.nombre().trim());
        nuevo.setCorreo(request.correo().trim().toLowerCase());
        nuevo.setPassword(passwordEncoder.encode(request.password()));
        nuevo.setRol("CLIENTE");
        nuevo.setRut(request.rut());
        nuevo.setTelefono(request.telefono());
        nuevo.setDireccion(request.direccion());
        nuevo.setActivo(true);

        Usuario guardado = usuarioRepository.save(nuevo);
        String token = jwtService.generarToken(guardado.getCorreo(), guardado.getRol());
        return new LoginResponse(token, guardado.getCorreo(), guardado.getNombre(), guardado.getRol());
    }
}

