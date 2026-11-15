package tienda_musica.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda_musica.example.backend.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
