package tienda_musica.pedido.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.pedido.dto.CrearPedidoRequest;
import tienda_musica.pedido.dto.ItemPedidoRequest;
import tienda_musica.pedido.exception.RecursoNoEncontradoException;
import tienda_musica.pedido.model.DetallePedido;
import tienda_musica.pedido.model.EstadoPedido;
import tienda_musica.pedido.model.Pedido;
import tienda_musica.pedido.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional(rollbackFor = Exception.class)
    public Pedido crearPedido(CrearPedidoRequest request) {
        Pedido pedido = Pedido.builder()
                .clienteId(request.getClienteId())
                .direccionEnvio(request.getDireccionEnvio())
                .estado(EstadoPedido.PENDIENTE)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();

        for (ItemPedidoRequest item : request.getItems()) {
            BigDecimal subtotal = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .productoId(item.getProductoId())
                    .nombreProducto(item.getNombreProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);
        }

        pedido.setTotal(total);
        pedido.setDetalles(detalles);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    public List<Pedido> listarMisPedidos(String clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    @Transactional
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));

        EstadoPedido estadoValido;
        try {
            estadoValido = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Estado invalido: '" + nuevoEstado + "'. Valores permitidos: PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO");
        }

        pedido.setEstado(estadoValido);
        return pedidoRepository.save(pedido);
    }
}
