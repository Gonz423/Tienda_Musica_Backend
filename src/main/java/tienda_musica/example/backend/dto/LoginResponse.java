package tienda_musica.example.backend.dto;

public record LoginResponse(
        String token,
        String correo,
        String nombre,
        String rol
) {}
