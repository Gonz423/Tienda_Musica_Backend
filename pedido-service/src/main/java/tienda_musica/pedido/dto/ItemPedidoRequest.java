package tienda_musica.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Ítem del carrito. El frontend solo indica QUÉ producto y CUÁNTAS unidades:
 * el nombre y el precio se leen desde producto-service para que el cliente
 * no pueda manipular el precio desde el navegador.
 */
@Data
public class ItemPedidoRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
