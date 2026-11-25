package tienda_musica.pedido.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import tienda_musica.pedido.exception.RecursoNoEncontradoException;
import tienda_musica.pedido.exception.ServicioNoDisponibleException;
import tienda_musica.pedido.security.JwtService;

import java.util.List;
import java.util.Map;

/**
 * Cliente REST hacia producto-service (comunicación síncrona entre microservicios).
 * - Lectura de productos: endpoint público GET /api/v1/productos/{id}.
 * - Descuento / reposición de stock: endpoints internos que exigen un token de servicio (rol SERVICIO).
 */
@Component
public class ProductoCliente {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductoRemoto(Long id, String codigo, String nombre, Integer precio, Integer stock) {}

    public record MovimientoStock(Long productoId, Integer cantidad) {}

    private final RestClient restClient;
    private final JwtService jwtService;

    public ProductoCliente(@Value("${app.servicios.producto-url:http://localhost:8083}") String baseUrl,
                           JwtService jwtService) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.jwtService = jwtService;
    }

    public ProductoRemoto obtenerProducto(Long id) {
        try {
            ProductoRemoto p = restClient.get()
                    .uri("/api/v1/productos/{id}", id)
                    .retrieve()
                    .body(ProductoRemoto.class);
            if (p == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
            }
            return p;
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El catálogo (producto-service) no está disponible");
        }
    }

    public void descontarStock(List<MovimientoStock> items) {
        enviarMovimiento("/api/v1/productos/stock/descontar", items);
    }

    public void reponerStock(List<MovimientoStock> items) {
        enviarMovimiento("/api/v1/productos/stock/reponer", items);
    }

    private void enviarMovimiento(String uri, List<MovimientoStock> items) {
        try {
            restClient.post()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(items)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            Map<?, ?> cuerpo = e.getResponseBodyAs(Map.class);
            Object msg = cuerpo != null ? cuerpo.get("error") : null;
            throw new IllegalArgumentException(msg != null ? msg.toString() : "Stock insuficiente");
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Uno de los productos del pedido ya no existe");
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El catálogo (producto-service) no está disponible");
        }
    }
}
