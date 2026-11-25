package tienda_musica.pedido.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tienda_musica.pedido.model.DetallePedido;
import tienda_musica.pedido.model.Pedido;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Cuando un pedido pasa a ENTREGADO se registra la venta en metrica-service.
 * Se reenvía el token del ADMIN que hizo el cambio de estado.
 * Es "best effort": si métricas está caído el pedido igual queda ENTREGADO.
 */
@Component
public class MetricaCliente {

    private static final Logger log = LoggerFactory.getLogger(MetricaCliente.class);

    private final RestClient restClient;

    public MetricaCliente(@Value("${app.servicios.metrica-url:http://localhost:8085}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void registrarVenta(Pedido pedido, String authorizationHeader) {
        int cantidadItems = pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum();
        String destacado = pedido.getDetalles().stream()
                .max(Comparator.comparing(DetallePedido::getCantidad))
                .map(DetallePedido::getNombreProducto)
                .orElse(null);

        Map<String, Object> body = new HashMap<>();
        body.put("pedidoId", pedido.getId());
        body.put("emailCliente", pedido.getClienteId());
        body.put("totalVenta", pedido.getTotal());
        body.put("cantidadItems", Math.max(1, cantidadItems));
        body.put("productoDestacado", destacado);
        body.put("fechaVenta", LocalDate.now().toString());

        try {
            restClient.post()
                    .uri("/api/v1/metricas/registro")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("No se pudo registrar la venta del pedido {} en metrica-service: {}", pedido.getId(), e.getMessage());
        }
    }
}
