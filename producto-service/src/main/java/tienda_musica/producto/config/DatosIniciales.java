package tienda_musica.producto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.repository.ProductoRepository;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DatosIniciales(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto(
                    "GTR-001", "Guitarras", "Fender Stratocaster Player", "Fender", "Player Plus",
                    12, 899990, "Guitarra eléctrica icónica con tres pastillas single-coil y diapasón de arce.",
                    "https://images.unsplash.com/photo-1550291652-6ea9114a47b1"
            ));

            productoRepository.save(new Producto(
                    "GTR-002", "Guitarras", "Gibson Les Paul Standard", "Gibson", "'60s Bourbon Burst",
                    6, 1799990, "Cuerpo de caoba con tapa de arce figurado y pastillas BurstBucker.",
                    "https://images.unsplash.com/photo-1516924962500-2b4b3b99ea02"
            ));

            productoRepository.save(new Producto(
                    "GTR-003", "Guitarras", "Yamaha F310 Acústica", "Yamaha", "F310 Natural",
                    25, 149990, "Excelente relación calidad-precio para principiantes y guitarristas intermedios.",
                    "https://images.unsplash.com/photo-1510915361894-db8b60106cb1"
            ));

            productoRepository.save(new Producto(
                    "BSS-001", "Bajos", "Fender Precision Bass", "Fender", "Player Series",
                    8, 849990, "El sonido legendario del bajo eléctrico moderno con pastilla split-coil.",
                    "https://images.unsplash.com/photo-1520523839898-507125cd53c1"
            ));

            productoRepository.save(new Producto(
                    "BSS-002", "Bajos", "Ibanez SR300E", "Ibanez", "SR Series 4 Cuerdas",
                    14, 389990, "Mástil ultra rápido de 5 piezas y electrónica activa versátil.",
                    "https://images.unsplash.com/photo-1445985543469-221e57446c70"
            ));

            productoRepository.save(new Producto(
                    "KEY-001", "Teclados", "Yamaha P-45 Piano Digital", "Yamaha", "P-45B 88 Teclas",
                    9, 499990, "Teclado contrapesado GHS y muestreo estéreo AWM para tacto realista.",
                    "https://images.unsplash.com/photo-1520523839898-507125cd53c1"
            ));

            productoRepository.save(new Producto(
                    "KEY-002", "Teclados", "Korg Minilogue XD", "Korg", "Sintetizador Analógico",
                    4, 699990, "Sintetizador polifónico analógico de 4 voces con multi-motor digital y efectos.",
                    "https://images.unsplash.com/photo-1511379938547-c1f69419868d"
            ));

            productoRepository.save(new Producto(
                    "DRM-001", "Baterias", "Roland TD-07KV V-Drums", "Roland", "TD-07KV",
                    5, 949990, "Batería electrónica con parches de malla de doble capa y módulo TD-07.",
                    "https://images.unsplash.com/photo-1519892300165-cb5542fb47c7"
            ));
        }
    }
}
