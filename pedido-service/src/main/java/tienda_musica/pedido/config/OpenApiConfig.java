package tienda_musica.pedido.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pedido Service - API REST (Sonido Vivo)",
                version = "1.0",
                description = "Checkout transaccional, bandeja de pedidos y máquina de estados. "
                        + "Usa el botón Authorize con el token obtenido en auth-service (POST /api/v1/auth/login)."
        ),
        servers = @Server(url = "http://localhost:8084", description = "Servidor Local - Puerto 8084")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
