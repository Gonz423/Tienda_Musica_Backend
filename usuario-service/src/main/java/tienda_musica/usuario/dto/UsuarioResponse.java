package tienda_musica.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tienda_musica.usuario.model.Usuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private String rut;
    private String telefono;
    private String direccion;
    private Boolean activo;

    public static UsuarioResponse fromEntity(Usuario u) {
        if (u == null) {
            return null;
        }
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rol(u.getRol())
                .rut(u.getRut())
                .telefono(u.getTelefono())
                .direccion(u.getDireccion())
                .activo(u.getActivo())
                .build();
    }
}
