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
                    "GE001", "Guitarras", "Fender Stratocaster Player", "Fender", "Player Plus",
                    12, 899990, "Guitarra electrica iconica con tres pastillas single-coil y diapason de arce.",
                    "GE001.jpg"
            ));

            productoRepository.save(new Producto(
                    "GE002", "Guitarras", "Gibson Les Paul Standard", "Gibson", "'60s Bourbon Burst",
                    6, 1799990, "Cuerpo de caoba con tapa de arce figurado y pastillas BurstBucker.",
                    "GE002.jpg"
            ));

            productoRepository.save(new Producto(
                    "GA001", "Guitarras", "Yamaha F310 Acustica", "Yamaha", "F310 Natural",
                    25, 149990, "Excelente relacion calidad-precio para principiantes y guitarristas intermedios.",
                    "GA001.jpg"
            ));

            productoRepository.save(new Producto(
                    "BA001", "Bajos", "Fender Precision Bass", "Fender", "Player Series",
                    8, 849990, "El sonido legendario del bajo electrico moderno con pastilla split-coil.",
                    "BA001.jpg"
            ));

            productoRepository.save(new Producto(
                    "BA002", "Bajos", "Ibanez SR300E", "Ibanez", "SR Series 4 Cuerdas",
                    14, 389990, "Mastil ultra rapido de 5 piezas y electronica activa versatil.",
                    "BA002.jpg"
            ));

            productoRepository.save(new Producto(
                    "TC001", "Teclados", "Yamaha P-45 Piano Digital", "Yamaha", "P-45B 88 Teclas",
                    9, 499990, "Teclado contrapesado GHS y muestreo estereo AWM para tacto realista.",
                    "TC001.jpg"
            ));

            productoRepository.save(new Producto(
                    "TC002", "Teclados", "Korg Minilogue XD", "Korg", "Sintetizador Analogico",
                    4, 699990, "Sintetizador polifonico analogico de 4 voces con multi-motor digital y efectos.",
                    "TC002.jpg"
            ));

            productoRepository.save(new Producto(
                    "BT001", "Baterias", "Roland TD-07KV V-Drums", "Roland", "TD-07KV",
                    5, 949990, "Bateria electronica con parches de malla de doble capa y modulo TD-07.",
                    "BT001.jpg"
            ));
        }
    }
}
