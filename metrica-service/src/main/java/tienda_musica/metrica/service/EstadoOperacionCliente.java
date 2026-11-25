package tienda_musica.metrica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Consulta en vivo a pedido-service y producto-service los indicadores operativos
 * (pedidos pendientes y productos con stock crítico), reenviando el token del ADMIN.
 * Si alguno no responde se devuelve 0 para no romper el panel de métricas.
 */
@Component
public class EstadoOperacionCliente {

    private static final Logger log = LoggerFactory.getLogger(EstadoOperacionCliente.class);
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA =
            new ParameterizedTypeReference<>() {};

    private final RestClient pedidos;
    private final RestClient productos;

    public EstadoOperacionCliente(@Value("${app.servicios.pedido-url:http://localhost:8084}") String pedidoUrl,
                                  @Value("${app.servicios.producto-url:http://localhost:8083}") String productoUrl) {
        this.pedidos = RestClient.builder().baseUrl(pedidoUrl).build();
        this.productos = RestClient.builder().baseUrl(productoUrl).build();
    }

    public long contarPedidosPendientes(String authorization) {
        try {
            List<Map<String, Object>> lista = pedidos.get().uri("/api/v1/pedidos")
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve().body(LISTA);
            if (lista == null) return 0;
            return lista.stream()
                    .filter(p -> {
                        Object e = p.get("estado");
                        return "PENDIENTE".equals(e) || "EN_PREPARACION".equals(e);
                    })
                    .count();
        } catch (RestClientException e) {
            log.warn("pedido-service no disponible para KPIs: {}", e.getMessage());
            return 0;
        }
    }

    public long contarStockCritico(String authorization) {
        try {
            List<Map<String, Object>> lista = productos.get().uri("/api/v1/productos/stock-critico")
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve().body(LISTA);
            return lista == null ? 0 : lista.size();
        } catch (RestClientException e) {
            log.warn("producto-service no disponible para KPIs: {}", e.getMessage());
            return 0;
        }
    }
}
