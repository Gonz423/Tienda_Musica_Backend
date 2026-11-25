package tienda_musica.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ítem de un movimiento de stock solicitado por pedido-service")
public class MovimientoStockItem {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(example = "1")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(example = "2")
    private Integer cantidad;
}
