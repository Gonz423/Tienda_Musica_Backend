package tienda_musica.metrica.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.metrica.dto.KpisResponse;
import tienda_musica.metrica.dto.RegistroVentaRequest;
import tienda_musica.metrica.model.RegistroVenta;
import tienda_musica.metrica.repository.RegistroVentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricaService {

    private final RegistroVentaRepository repo;

    // ── Registrar venta (cuando pedido llega a ENTREGADO) ─────────────────
    @Transactional
    public RegistroVenta registrarVenta(RegistroVentaRequest req) {
        if (repo.existsByPedidoId(req.getPedidoId())) {
            throw new IllegalArgumentException("Ya existe un registro para el pedido: " + req.getPedidoId());
        }
        RegistroVenta r = RegistroVenta.builder()
                .pedidoId(req.getPedidoId())
                .emailCliente(req.getEmailCliente())
                .totalVenta(req.getTotalVenta())
                .descuentoAplicado(req.getDescuentoAplicado() != null ? req.getDescuentoAplicado() : BigDecimal.ZERO)
                .cantidadItems(req.getCantidadItems())
                .productoDestacado(req.getProductoDestacado())
                .fechaVenta(req.getFechaVenta() != null ? req.getFechaVenta() : LocalDate.now())
                .build();
        return repo.save(r);
    }

    // ── KPIs globales (todos los registros) ───────────────────────────────
    @Transactional(readOnly = true)
    public KpisResponse obtenerKpis() {
        LocalDate inicio = LocalDate.of(2000, 1, 1);
        LocalDate fin = LocalDate.now();
        return calcularKpis(inicio, fin);
    }

    // ── KPIs del día ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public KpisResponse kpisHoy() {
        LocalDate hoy = LocalDate.now();
        return calcularKpis(hoy, hoy);
    }

    // ── KPIs del mes ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public KpisResponse kpisMesActual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        return calcularKpis(inicio, fin);
    }

    // ── KPIs por rango ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public KpisResponse calcularKpis(LocalDate inicio, LocalDate fin) {
        BigDecimal totalIngresos = repo.sumTotalVentaByFechaVentaBetween(inicio, fin);
        if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;

        long cantidadPedidos = repo.countPedidosByFechaVentaBetween(inicio, fin);
        Long totalItems = repo.sumCantidadItemsByFechaVentaBetween(inicio, fin);
        if (totalItems == null) totalItems = 0L;

        BigDecimal ticketPromedio = cantidadPedidos > 0
                ? totalIngresos.divide(BigDecimal.valueOf(cantidadPedidos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Producto mas vendido
        List<String> topProductos = repo.findTopProductos();
        String productoMasVendido = topProductos.isEmpty() ? "Sin ventas aun" : topProductos.get(0);

        return KpisResponse.builder()
                .totalIngresos(totalIngresos)
                .cantidadTotalPedidos(cantidadPedidos)
                .pedidosPendientes(0L) // se actualiza via registro desde pedido-service
                .productosStockCritico(0L) // se actualiza via registro desde producto-service
                .productoMasVendido(productoMasVendido)
                .ticketPromedio(ticketPromedio)
                .build();
    }
}
