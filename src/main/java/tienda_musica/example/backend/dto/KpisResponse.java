package tienda_musica.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tienda_musica.example.backend.model.Pedido;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpisResponse {
    private BigDecimal totalIngresos;
    private long cantidadTotalPedidos;
    private long pedidosPendientes;
    private long productosStockCritico;
    private String productoMasVendido;
    private List<Pedido> ultimosPedidos;
}
