package tienda_musica.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tienda_musica.auth.model.Usuario;
import tienda_musica.auth.repository.UsuarioRepository;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("Administrador", "admin@tiendamusica.cl", "Admin123!", "ADMIN", "11.111.111-1");
        crearUsuarioSiNoExiste("Vendedor Bodega", "empleado@tiendamusica.cl", "Vendedor123!", "VENDEDOR", "22.222.222-2");
        crearUsuarioSiNoExiste("Cliente Prueba", "cliente@tiendamusica.cl", "Cliente123!", "CLIENTE", "33.333.333-3");
    }

    private void crearUsuarioSiNoExiste(String nombre, String correo, String passwordPlana, String rol, String rut) {
        if (!usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setCorreo(correo);
            u.setPassword(passwordEncoder.encode(passwordPlana));
            u.setRol(rol);
            u.setRut(rut);
            u.setActivo(true);
            usuarioRepository.save(u);
        }
    }
}
