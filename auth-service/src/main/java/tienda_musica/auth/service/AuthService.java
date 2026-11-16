package tienda_musica.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.auth.dto.LoginRequest;
import tienda_musica.auth.dto.LoginResponse;
import tienda_musica.auth.dto.RegistroRequest;
import tienda_musica.auth.model.Usuario;
import tienda_musica.auth.repository.UsuarioRepository;
import tienda_musica.auth.security.JwtService;

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

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setCorreo(request.correo());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol("CLIENTE");
        usuario.setRut(request.rut());
        usuario.setTelefono(request.telefono());
        usuario.setDireccion(request.direccion());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
        return new LoginResponse(token, usuario.getCorreo(), usuario.getNombre(), usuario.getRol());
    }
}
