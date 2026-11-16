package tienda_musica.auth.dto;

public record LoginResponse(
        String token,
        String correo,
        String nombre,
        String rol
) {}
