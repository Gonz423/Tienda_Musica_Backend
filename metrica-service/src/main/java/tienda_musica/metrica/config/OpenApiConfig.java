package tienda_musica.metrica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Metrica Service - API REST (Sonido Vivo)",
                version = "1.0",
                description = "KPIs de ventas para el administrador. "
                        + "Usa el botón Authorize con el token obtenido en auth-service (POST /api/v1/auth/login)."
        ),
        servers = @Server(url = "http://localhost:8085", description = "Servidor Local - Puerto 8085")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
