package tienda_musica.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
