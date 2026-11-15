package tienda_musica.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.example.backend.dto.CrearPedidoRequest;
import tienda_musica.example.backend.dto.ItemPedidoRequest;
import tienda_musica.example.backend.exception.RecursoNoEncontradoException;
import tienda_musica.example.backend.exception.StockInsuficienteException;
import tienda_musica.example.backend.model.DetallePedido;
import tienda_musica.example.backend.model.EstadoPedido;
import tienda_musica.example.backend.model.Pedido;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.repository.PedidoRepository;
import tienda_musica.example.backend.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

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
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para '" + producto.getNombre() +
                        "'. Disponible: " + producto.getStock() +
                        ", solicitado: " + item.getCantidad());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(precioUnitario)
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

    @Transactional
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));

        EstadoPedido estadoValido;
        try {
            estadoValido = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Estado inválido: '" + nuevoEstado + "'. Valores permitidos: PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO");
        }

        pedido.setEstado(estadoValido);
        return pedidoRepository.save(pedido);
    }
}
