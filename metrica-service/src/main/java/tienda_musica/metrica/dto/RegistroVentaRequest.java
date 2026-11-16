package tienda_musica.metrica.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegistroVentaRequest {
    @NotNull
    private Long pedidoId;

    @NotBlank
    private String emailCliente;

    @NotNull
    private BigDecimal totalVenta;

    private BigDecimal descuentoAplicado;

    @Min(1)
    private int cantidadItems;

    // nombre del producto mas vendido del pedido (para estadisticas)
    private String productoDestacado;

    private LocalDate fechaVenta;
}
