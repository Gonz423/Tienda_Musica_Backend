package tienda_musica.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * El cliente dueño del pedido NO viene en el cuerpo: se toma del token JWT
 * (claim "sub" = correo) para que nadie pueda crear pedidos a nombre de otro.
 */
@Data
public class CrearPedidoRequest {

    @NotBlank(message = "La direccion de envio es obligatoria")
    private String direccionEnvio;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<ItemPedidoRequest> items;
}
