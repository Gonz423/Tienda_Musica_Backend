package tienda_musica.usuario.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Usuario Service - API REST (Sonido Vivo)",
                version = "1.0",
                description = "Microservicio de Gestión de Usuarios, Roles, Estados y Auditoría para el Rol Jefe (ADMIN).",
                contact = @Contact(name = "Equipo Sonido Vivo", email = "contacto@tiendamusica.cl")
        ),
        servers = @Server(url = "http://localhost:8082", description = "Servidor Local - Puerto 8082"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
