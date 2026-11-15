package tienda_musica.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoRequest {

    @NotBlank(message = "El clienteId (o correo) es obligatorio")
    private String clienteId;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<ItemPedidoRequest> items;
}
