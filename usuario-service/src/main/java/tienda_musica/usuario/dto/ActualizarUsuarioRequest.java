package tienda_musica.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato de correo no es válido")
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    private String rol; // ADMIN, VENDEDOR, CLIENTE

    private String rut;
    private String telefono;
    private String direccion;
    private Boolean activo;

    // Opcional: si viene con texto se reemplaza la contraseña (se guarda con BCrypt)
    private String password;

    public ActualizarUsuarioRequest(String nombre, String correo, String rol, String rut,
                                    String telefono, String direccion, Boolean activo) {
        this(nombre, correo, rol, rut, telefono, direccion, activo, null);
    }
}
