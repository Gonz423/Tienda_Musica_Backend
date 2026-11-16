package tienda_musica.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Endpoints de catálogo y consulta de instrumentos musicales")
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

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna el detalle de un instrumento musical por su identificador")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener producto por código", description = "Retorna el detalle de un instrumento musical por su código único")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.buscarPorCodigo(codigo));
    }
}
