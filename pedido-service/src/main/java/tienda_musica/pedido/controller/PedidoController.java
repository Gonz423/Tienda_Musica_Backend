package tienda_musica.pedido.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestion transaccional de ordenes de compra")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    private static boolean esPersonal(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDEDOR"));
    }

    // POST /api/v1/pedidos — cualquier usuario autenticado (el cliente sale del token)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Crear nuevo pedido (checkout)",
            description = "El cliente se toma del token JWT. Nombre y precio se leen desde producto-service "
                    + "y el stock se descuenta en la misma operación.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Algún producto no existe"),
            @ApiResponse(responseCode = "503", description = "producto-service no disponible")
    })
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request,
                                              Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(request, authentication.getName()));
    }

    // GET /api/v1/pedidos — ADMIN y VENDEDOR ven todos los pedidos (bandeja)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Listar todos los pedidos (ADMIN/VENDEDOR)")
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/v1/pedidos/mis-pedidos — cliente ve sus propios pedidos (correo desde el token)
    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar pedidos del usuario autenticado")
    public ResponseEntity<List<Pedido>> listarMisPedidos(Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarMisPedidos(authentication.getName()));
    }

    // GET /api/v1/pedidos/{id} — dueño del pedido o personal (ADMIN/VENDEDOR)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener pedido por ID", description = "Un CLIENTE solo puede ver sus propios pedidos (403 en otro caso).")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(pedidoService.buscarPorIdAutorizado(id, authentication.getName(), esPersonal(authentication)));
    }

    // PATCH /api/v1/pedidos/{id}/estado — máquina de estados
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado del pedido (solo ADMIN; el VENDEDOR solo visualiza)",
               description = "Transiciones válidas: PENDIENTE -> EN_PREPARACION -> ENVIADO -> ENTREGADO; "
                       + "PENDIENTE/EN_PREPARACION -> CANCELADO (repone stock). Al llegar a ENTREGADO se registra la venta en metrica-service.")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request.getEstado(), authorization));
    }

    // DELETE /api/v1/pedidos/{id} — solo ADMIN, solo pedidos cerrados
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar pedido (ADMIN)", description = "Solo pedidos en estado CANCELADO o ENTREGADO.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
