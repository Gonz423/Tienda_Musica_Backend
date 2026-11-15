package tienda_musica.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                    Producto.builder().nombre("Guitarra Acústica Clásica").precio(new BigDecimal("129990")).stock(10).stockCritico(3).build(),
                    Producto.builder().nombre("Guitarra Eléctrica Stratocaster").precio(new BigDecimal("249990")).stock(5).stockCritico(2).build(),
                    Producto.builder().nombre("Bajo Eléctrico 4 Cuerdas").precio(new BigDecimal("219990")).stock(4).stockCritico(2).build(),
                    Producto.builder().nombre("Batería Acústica 5 Piezas").precio(new BigDecimal("459990")).stock(2).stockCritico(2).build(),
                    Producto.builder().nombre("Teclado Digital 61 Teclas").precio(new BigDecimal("179990")).stock(8).stockCritico(3).build()
            ));
        }
    }
}
