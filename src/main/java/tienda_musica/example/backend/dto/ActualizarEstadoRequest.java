package tienda_musica.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
