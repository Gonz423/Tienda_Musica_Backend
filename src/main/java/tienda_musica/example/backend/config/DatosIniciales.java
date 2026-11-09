package tienda_musica.example.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tienda_musica.example.backend.model.Usuario;
import tienda_musica.example.backend.repository.UsuarioRepository;

@Configuration
public class DatosIniciales {

    @Bean
    public CommandLineRunner poblarUsuariosIniciales(UsuarioRepository usuarioRepository,
                                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario Administrador (Jefe)
            if (usuarioRepository.findByCorreoIgnoreCase("admin@tiendamusica.cl").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador Jefe");
                admin.setCorreo("admin@tiendamusica.cl");
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setRol("ADMIN");
                admin.setRut("11.111.111-1");
                admin.setTelefono("+56 9 1111 2222");
                admin.setDireccion("Av. Providencia 1234, Santiago");
                admin.setActivo(true);
                usuarioRepository.save(admin);
            }

            // Usuario Empleado (Vendedor / Bodega)
            if (usuarioRepository.findByCorreoIgnoreCase("empleado@tiendamusica.cl").isEmpty()) {
                Usuario empleado = new Usuario();
                empleado.setNombre("Vendedor Bodega");
                empleado.setCorreo("empleado@tiendamusica.cl");
                empleado.setPassword(passwordEncoder.encode("Vendedor123!"));
                empleado.setRol("VENDEDOR");
                empleado.setRut("22.222.222-2");
                empleado.setTelefono("+56 9 3333 4444");
                empleado.setDireccion("Calle Las Melodías 567, Santiago");
                empleado.setActivo(true);
                usuarioRepository.save(empleado);
            }

            // Usuario Cliente Demo
            if (usuarioRepository.findByCorreoIgnoreCase("cliente@tiendamusica.cl").isEmpty()) {
                Usuario cliente = new Usuario();
                cliente.setNombre("Cliente Melómano");
                cliente.setCorreo("cliente@tiendamusica.cl");
                cliente.setPassword(passwordEncoder.encode("Cliente123!"));
                cliente.setRol("CLIENTE");
                cliente.setRut("18.888.888-8");
                cliente.setTelefono("+56 9 5555 6666");
                cliente.setDireccion("Pasaje Los Acordes 890, Santiago");
                cliente.setActivo(true);
                usuarioRepository.save(cliente);
            }
        };
    }
}
