package tienda_musica.pedido.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tienda_musica.pedido.dto.ActualizarEstadoRequest;
import tienda_musica.pedido.dto.CrearPedidoRequest;
import tienda_musica.pedido.model.Pedido;
import tienda_musica.pedido.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestion transaccional de ordenes de compra")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    // POST /api/pedidos — cualquier usuario autenticado (cliente hace checkout)
    @PostMapping
    @Operation(summary = "Crear nuevo pedido (checkout)", description = "Crea un pedido con sus detalles. Devuelve 201 con el detalle completo.")
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(request));
    }

    // GET /api/pedidos — ADMIN y VENDEDOR ven todos los pedidos (bandeja)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Listar todos los pedidos (ADMIN/VENDEDOR)")
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/mis-pedidos — cliente ve sus propios pedidos
    @GetMapping("/mis-pedidos")
    @Operation(summary = "Listar pedidos del cliente autenticado")
    public ResponseEntity<List<Pedido>> listarMisPedidos(
            Authentication authentication,
            @RequestParam(required = false) String clienteId) {
        String correo = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : clienteId;
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("No se pudo identificar al cliente autenticado.");
        }
        return ResponseEntity.ok(pedidoService.listarMisPedidos(correo));
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    // PATCH /api/pedidos/{id}/estado — VENDEDOR avanza estado (PENDIENTE -> EN_PREPARACION -> ENVIADO -> ENTREGADO)
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Actualizar estado del pedido (ADMIN/VENDEDOR)",
               description = "Maquina de estados: PENDIENTE -> EN_PREPARACION -> ENVIADO -> ENTREGADO")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request.getEstado()));
    }
}
