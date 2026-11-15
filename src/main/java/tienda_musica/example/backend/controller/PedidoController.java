package tienda_musica.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tienda_musica.example.backend.dto.ActualizarEstadoRequest;
import tienda_musica.example.backend.dto.CrearPedidoRequest;
import tienda_musica.example.backend.model.Pedido;
import tienda_musica.example.backend.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión transaccional de órdenes de compra")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Valida y descuenta stock de forma atómica; devuelve 201 Created con el detalle.")
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido nuevoPedido = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Listar todos los pedidos", description = "Lista todos los pedidos registrados en MySQL (roles: ADMIN, VENDEDOR).")
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/mis-pedidos")
    @Operation(summary = "Listar mis pedidos", description = "Lista los pedidos del cliente autenticado según su correo en el token JWT.")
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

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado (PENDIENTE -> EN_PREPARACION -> ENVIADO -> ENTREGADO).")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request.getEstado()));
    }
}
