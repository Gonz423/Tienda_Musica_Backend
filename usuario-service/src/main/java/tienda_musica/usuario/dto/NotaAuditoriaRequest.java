package tienda_musica.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaAuditoriaRequest {

    @NotBlank(message = "La nota u observación es obligatoria")
    private String nota;
}
