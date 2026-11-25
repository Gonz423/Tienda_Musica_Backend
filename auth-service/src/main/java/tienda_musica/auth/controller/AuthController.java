package tienda_musica.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tienda_musica.auth.dto.LoginRequest;
import tienda_musica.auth.dto.LoginResponse;
import tienda_musica.auth.dto.RegistroRequest;
import tienda_musica.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y registro de clientes")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y retorna un token JWT con los datos del usuario")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo cliente", description = "Crea una nueva cuenta de cliente con contraseña encriptada en BCrypt y retorna sesión")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Renovar token JWT",
            description = "Recibe un token vigente en el header Authorization y emite uno nuevo. "
                    + "Vuelve a consultar la base de datos: si la cuenta fue desactivada se rechaza (401) "
                    + "y si el rol cambió, el nuevo token ya trae el rol actualizado.")
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(authService.refrescar(authorization));
    }
}
