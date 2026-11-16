package tienda_musica.example.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.model.Usuario;
import tienda_musica.example.backend.repository.ProductoRepository;
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

    @Bean
    public CommandLineRunner poblarProductosIniciales(ProductoRepository productoRepository) {
        return args -> {
            if (productoRepository.count() == 0) {
                // Guitarras Acústicas
                productoRepository.save(new Producto(
                        "GA001", "Guitarras Acústicas", "Guitarra Acústica Folk",
                        "Yamaha", "F310", 8, 129990,
                        "Tapa de abeto, aros y fondo de meranti. Ideal para iniciantes.",
                        "/assets/img/GA001.jpg"
                ));
                productoRepository.save(new Producto(
                        "GA002", "Guitarras Acústicas", "Guitarra Acústica Dreadnought",
                        "Fender", "CD-60S", 5, 189990,
                        "Tapa de abeto macizo, brazo de caoba. Sonido cálido y proyectado.",
                        "/assets/img/GA002.jpg"
                ));

                // Guitarras Eléctricas
                productoRepository.save(new Producto(
                        "GE001", "Guitarras Eléctricas", "Guitarra Eléctrica Stratocaster",
                        "Squier", "Affinity Strat", 5, 249990,
                        "Cuerpo de álamo, mástil de arce, pastillas SSS.",
                        "/assets/img/GE001.jpg"
                ));
                productoRepository.save(new Producto(
                        "GE002", "Guitarras Eléctricas", "Guitarra Eléctrica Les Paul",
                        "Epiphone", "Les Paul Std", 4, 329990,
                        "Cuerpo caoba, tapa arce, pastillas humbucker.",
                        "/assets/img/GE002.jpg"
                ));

                // Baterías
                productoRepository.save(new Producto(
                        "BA001", "Baterías", "Batería Acústica Stage Custom",
                        "Yamaha", "SBP2F5", 3, 799990,
                        "Cascos 100% abedul, acabado lacado brillante.",
                        "/assets/img/BA001.jpg"
                ));
                productoRepository.save(new Producto(
                        "BA002", "Baterías", "Batería Electrónica V-Drums",
                        "Roland", "TD-07KV", 4, 649990,
                        "Pads de malla de doble capa, módulo Bluetooth integrado.",
                        "/assets/img/BA002.jpg"
                ));

                // Teclados y Pianos
                productoRepository.save(new Producto(
                        "PE001", "Teclados", "Piano Digital 88 Teclas",
                        "Yamaha", "P-45", 6, 449990,
                        "Teclado martillo graduado GHS, sonido estéreo AWM.",
                        "/assets/img/PE001.jpg"
                ));

                // Accesorios
                productoRepository.save(new Producto(
                        "AC001", "Accesorios", "Afinador Cromático de Pinza",
                        "Korg", "Pitchclip 2", 20, 15990,
                        "Diseño compacto y pantalla LED de alta visibilidad.",
                        "/assets/img/AC001.jpg"
                ));
            }
        };
    }
}
