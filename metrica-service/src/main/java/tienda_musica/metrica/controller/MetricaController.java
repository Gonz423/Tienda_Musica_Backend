package tienda_musica.metrica.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda_musica.metrica.dto.KpisResponse;
import tienda_musica.metrica.dto.RegistroVentaRequest;
import tienda_musica.metrica.model.RegistroVenta;
import tienda_musica.metrica.service.MetricaService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/metricas")
@RequiredArgsConstructor
@Tag(name = "Metricas", description = "KPIs y analitica de ventas (solo ADMIN/Jefe)")
@SecurityRequirement(name = "bearerAuth")
public class MetricaController {

    private final MetricaService metricaService;

    // POST /api/v1/metricas/registro — registrar venta entregada
    @PostMapping("/registro")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar venta completada (pedido ENTREGADO)")
    public ResponseEntity<RegistroVenta> registrar(@Valid @RequestBody RegistroVentaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metricaService.registrarVenta(req));
    }

    // GET /api/v1/metricas/kpis — KPIs globales (igual que el diseño de Damian)
    @GetMapping("/kpis")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "KPIs globales del negocio (solo ADMIN)",
               description = "Devuelve ingresos totales, ticket promedio, total pedidos y producto mas vendido.")
    public ResponseEntity<KpisResponse> obtenerKpis(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(metricaService.conEstadoOperacion(metricaService.obtenerKpis(), authorization));
    }

    // GET /api/v1/metricas/kpi/hoy
    @GetMapping("/kpi/hoy")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "KPIs del dia de hoy (solo ADMIN)")
    public ResponseEntity<KpisResponse> kpiHoy(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(metricaService.conEstadoOperacion(metricaService.kpisHoy(), authorization));
    }

    // GET /api/v1/metricas/kpi/mes
    @GetMapping("/kpi/mes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "KPIs del mes actual (solo ADMIN)")
    public ResponseEntity<KpisResponse> kpiMes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(metricaService.conEstadoOperacion(metricaService.kpisMesActual(), authorization));
    }

    // GET /api/v1/metricas/kpi?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
    @GetMapping("/kpi")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "KPIs por rango de fechas (solo ADMIN)")
    public ResponseEntity<KpisResponse> kpiRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(metricaService.conEstadoOperacion(metricaService.calcularKpis(inicio, fin), authorization));
    }
}
