package tienda_musica.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tienda_musica.example.backend.dto.KpisResponse;
import tienda_musica.example.backend.service.MetricasService;

@RestController
@RequestMapping("/api/metricas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Métricas", description = "KPIs y reportabilidad de negocio")
public class MetricasController {

    private final MetricasService metricasService;

    @GetMapping("/kpis")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener KPIs de negocio", description = "Devuelve ingresos totales, total de pedidos, pendientes, stock crítico y producto más vendido (solo ADMIN).")
    public ResponseEntity<KpisResponse> obtenerKpis() {
        return ResponseEntity.ok(metricasService.obtenerKpis());
    }
}
