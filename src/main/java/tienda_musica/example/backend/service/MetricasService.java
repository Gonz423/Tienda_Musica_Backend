package tienda_musica.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tienda_musica.example.backend.dto.KpisResponse;
import tienda_musica.example.backend.model.DetallePedido;
import tienda_musica.example.backend.model.EstadoPedido;
import tienda_musica.example.backend.model.Pedido;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.repository.PedidoRepository;
import tienda_musica.example.backend.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetricasService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public KpisResponse obtenerKpis() {
        List<Pedido> todosLosPedidos = pedidoRepository.findAllByOrderByFechaDesc();

        BigDecimal totalIngresos = todosLosPedidos.stream()
                .map(Pedido::getTotal)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadTotalPedidos = todosLosPedidos.size();
        long pedidosPendientes = pedidoRepository.countByEstado(EstadoPedido.PENDIENTE);

        List<Producto> productos = productoRepository.findAll();
        long productosStockCritico = productos.stream()
                .filter(p -> {
                    int critico = p.getStockCritico() != null ? p.getStockCritico() : 5;
                    return p.getStock() != null && p.getStock() <= critico;
                })
                .count();

        Map<String, Integer> conteoProductos = new HashMap<>();
        for (Pedido pedido : todosLosPedidos) {
            if (pedido.getDetalles() != null) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    if (detalle.getProducto() != null) {
                        String nombre = detalle.getProducto().getNombre();
                        conteoProductos.merge(nombre, detalle.getCantidad(), Integer::sum);
                    }
                }
            }
        }

        String productoMasVendido = conteoProductos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin ventas aún");

        List<Pedido> ultimosPedidos = todosLosPedidos.stream()
                .limit(10)
                .toList();

        return KpisResponse.builder()
                .totalIngresos(totalIngresos)
                .cantidadTotalPedidos(cantidadTotalPedidos)
                .pedidosPendientes(pedidosPendientes)
                .productosStockCritico(productosStockCritico)
                .productoMasVendido(productoMasVendido)
                .ultimosPedidos(ultimosPedidos)
                .build();
    }
}
