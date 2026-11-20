package tienda_musica.producto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tienda_musica.producto.dto.AjustarStockRequest;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Endpoints de catálogo, consulta y gestión de stock de instrumentos musicales")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar catálogo de productos", description = "Retorna todos los productos con filtros opcionales de categoría y término de búsqueda")
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda
    ) {
        return ResponseEntity.ok(productoService.listar(categoria, busqueda));
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar productos con stock crítico (<= 3)", description = "Retorna únicamente los productos cuyo stock es menor o igual a 3 unidades. Accesible por ADMIN y VENDEDOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de productos con stock crítico obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes: se requiere ADMIN o VENDEDOR")
    })
    public ResponseEntity<List<Producto>> listarStockCritico() {
        return ResponseEntity.ok(productoService.listarStockCritico());
    }

    @GetMapping("/{id:[0-9]+}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna el detalle de un instrumento musical por su identificador")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener producto por código", description = "Retorna el detalle de un instrumento musical por su código único")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.buscarPorCodigo(codigo));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto", description = "Crea un nuevo instrumento musical. Requiere rol ADMIN.")
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(producto));
    }

    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un instrumento musical existente. Requiere rol ADMIN.")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizar(id, producto));
    }

    @PatchMapping("/{id:[0-9]+}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ajustar stock (+/-)", description = "Permite incrementar o decrementar el stock disponible de un producto (+/- unidades). Accesible por ADMIN y VENDEDOR. No permite stock negativo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock ajustado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Ajuste inválido o stock resultante negativo"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes: se requiere ADMIN o VENDEDOR")
    })
    public ResponseEntity<Producto> ajustarStock(
            @PathVariable Long id,
            @RequestBody(required = false) AjustarStockRequest request,
            @RequestParam(required = false) Integer cantidad
    ) {
        int delta;
        if (request != null && request.getCantidad() != null) {
            delta = request.obtenerDeltaEfectivo();
        } else if (cantidad != null) {
            delta = cantidad;
        } else {
            throw new IllegalArgumentException("Debe proporcionar la cantidad a ajustar en el cuerpo de la solicitud o como parámetro 'cantidad'");
        }
        return ResponseEntity.ok(productoService.ajustarStock(id, delta));
    }

    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto", description = "Elimina un instrumento musical por su ID. Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
