package tienda_musica.pedido.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.pedido.client.MetricaCliente;
import tienda_musica.pedido.client.ProductoCliente;
import tienda_musica.pedido.client.ProductoCliente.MovimientoStock;
import tienda_musica.pedido.client.ProductoCliente.ProductoRemoto;
import tienda_musica.pedido.dto.CrearPedidoRequest;
import tienda_musica.pedido.dto.ItemPedidoRequest;
import tienda_musica.pedido.exception.RecursoNoEncontradoException;
import tienda_musica.pedido.model.DetallePedido;
import tienda_musica.pedido.model.EstadoPedido;
import tienda_musica.pedido.model.Pedido;
import tienda_musica.pedido.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PedidoService {

    /**
     * Máquina de estados del pedido: solo se permiten estas transiciones.
     * PENDIENTE -> EN_PREPARACION -> ENVIADO -> ENTREGADO
     * PENDIENTE / EN_PREPARACION -> CANCELADO (repone stock)
     */
    private static final Map<EstadoPedido, Set<EstadoPedido>> TRANSICIONES = new EnumMap<>(EstadoPedido.class);

    static {
        TRANSICIONES.put(EstadoPedido.PENDIENTE, Set.of(EstadoPedido.EN_PREPARACION, EstadoPedido.CANCELADO));
        TRANSICIONES.put(EstadoPedido.EN_PREPARACION, Set.of(EstadoPedido.ENVIADO, EstadoPedido.CANCELADO));
        TRANSICIONES.put(EstadoPedido.ENVIADO, Set.of(EstadoPedido.ENTREGADO));
        TRANSICIONES.put(EstadoPedido.ENTREGADO, Set.of());
        TRANSICIONES.put(EstadoPedido.CANCELADO, Set.of());
    }

    private final PedidoRepository pedidoRepository;
    private final ProductoCliente productoCliente;
    private final MetricaCliente metricaCliente;

    /**
     * Checkout transaccional:
     * 1. Agrupa ítems repetidos del carrito.
     * 2. Lee nombre y precio REAL de cada producto desde producto-service (no se confía en el frontend).
     * 3. Guarda el pedido y luego descuenta el stock (atómico en producto-service).
     *    Si el descuento falla (sin stock) se lanza excepción y el pedido hace rollback.
     */
    @Transactional(rollbackFor = Exception.class)
    public Pedido crearPedido(CrearPedidoRequest request, String correoCliente) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemPedidoRequest item : request.getItems()) {
            cantidades.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
        }

        Pedido pedido = Pedido.builder()
                .clienteId(correoCliente)
                .direccionEnvio(request.getDireccionEnvio().trim())
                .estado(EstadoPedido.PENDIENTE)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();
        List<MovimientoStock> movimientos = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
            ProductoRemoto producto = productoCliente.obtenerProducto(entry.getKey());
            int cantidad = entry.getValue();
            int stock = producto.stock() != null ? producto.stock() : 0;
            if (stock < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para \"" + producto.nombre()
                        + "\". Disponible: " + stock + ", solicitado: " + cantidad);
            }

            BigDecimal precio = BigDecimal.valueOf(producto.precio());
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            detalles.add(DetallePedido.builder()
                    .pedido(pedido)
                    .productoId(producto.id())
                    .nombreProducto(producto.nombre())
                    .cantidad(cantidad)
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build());
            movimientos.add(new MovimientoStock(producto.id(), cantidad));
        }

        pedido.setTotal(total);
        pedido.setDetalles(detalles);
        Pedido guardado = pedidoRepository.save(pedido);

        // Si no hay stock suficiente aquí se lanza la excepción y el INSERT del pedido se revierte
        productoCliente.descontarStock(movimientos);

        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarMisPedidos(String clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    /** Un CLIENTE solo puede ver sus propios pedidos; ADMIN y VENDEDOR ven cualquiera. */
    @Transactional(readOnly = true)
    public Pedido buscarPorIdAutorizado(Long id, String correo, boolean esPersonal) {
        Pedido pedido = buscarPorId(id);
        if (!esPersonal && !pedido.getClienteId().equalsIgnoreCase(correo)) {
            throw new AccessDeniedException("No tienes permiso para ver este pedido");
        }
        return pedido;
    }

    @Transactional
    public Pedido actualizarEstado(Long id, String nuevoEstado, String authorizationHeader) {
        Pedido pedido = buscarPorId(id);

        EstadoPedido destino;
        try {
            destino = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Estado invalido: '" + nuevoEstado + "'. Valores permitidos: PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO");
        }

        EstadoPedido actual = pedido.getEstado();
        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(destino)) {
            throw new IllegalArgumentException("Transicion no permitida: " + actual + " -> " + destino);
        }

        pedido.setEstado(destino);
        Pedido guardado = pedidoRepository.save(pedido);

        if (destino == EstadoPedido.CANCELADO) {
            productoCliente.reponerStock(guardado.getDetalles().stream()
                    .map(d -> new MovimientoStock(d.getProductoId(), d.getCantidad()))
                    .toList());
        } else if (destino == EstadoPedido.ENTREGADO) {
            metricaCliente.registrarVenta(guardado, authorizationHeader);
        }
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido.getEstado() != EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Solo se pueden eliminar pedidos CANCELADOS o ENTREGADOS (estado actual: "
                    + pedido.getEstado() + ")");
        }
        pedidoRepository.delete(pedido);
    }
}
