package tienda_musica.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda_musica.example.backend.model.EstadoPedido;
import tienda_musica.example.backend.model.Pedido;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaDesc(String clienteId);
    List<Pedido> findAllByOrderByFechaDesc();
    long countByEstado(EstadoPedido estado);
}
