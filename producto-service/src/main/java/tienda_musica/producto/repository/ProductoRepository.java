package tienda_musica.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda_musica.producto.model.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Producto> findByCategoriaIgnoreCase(String categoria);

    List<Producto> findByNombreContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrModeloContainingIgnoreCase(
            String nombre, String marca, String modelo
    );

    List<Producto> findByStockLessThanEqualOrderByStockAsc(Integer limite);
}
