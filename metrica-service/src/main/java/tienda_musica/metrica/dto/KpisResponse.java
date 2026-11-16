package tienda_musica.metrica.dto;

import lombok.*;

import java.math.BigDecimal;

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
    // ticket promedio = totalIngresos / cantidadTotalPedidos
    private BigDecimal ticketPromedio;
}
