package tienda_musica.pedido.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer:tienda-musica-auth}")
    private String issuer;

    private SecretKey clave() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (Exception e) {
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerCorreo(String token) {
        return validar(token).getSubject();
    }

    public String obtenerRol(String token) {
        return validar(token).get("rol", String.class);
    }

    public boolean esTokenValido(String token) {
        try {
            validar(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Token de corta duración (60 s) que pedido-service usa para llamar a los
     * endpoints internos de producto-service (descontar / reponer stock).
     * Rol SERVICIO: ningún usuario humano puede tener este rol.
     */
    public String generarTokenServicio() {
        Date ahora = new Date();
        return Jwts.builder()
                .issuer(issuer)
                .subject("pedido-service")
                .claim("rol", "SERVICIO")
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + 60_000))
                .signWith(clave())
                .compact();
    }
}
