# Despliegue en AWS EC2 (guía 18)

Una instancia EC2 Ubuntu con:

| Componente | Puerto | Acceso |
|---|---|---|
| Nginx + React (`/var/www/tienda-musica`) | 80 | Público |
| auth-service | 8081 | Solo 127.0.0.1 |
| usuario-service | 8082 | Solo 127.0.0.1 |
| producto-service | 8083 | Solo 127.0.0.1 |
| pedido-service | 8084 | Solo 127.0.0.1 |
| metrica-service | 8085 | Solo 127.0.0.1 |
| MariaDB | 3306 | Solo local |
| SSH | 22 | Solo tu IP |

Security group: **22 desde tu IP** y **80 desde Internet**. No abrir 3306 ni 8081-8085.

## Rutas públicas (Nginx)

| Ruta pública | Destino interno |
|---|---|
| `/api/auth-service/**` | `127.0.0.1:8081/api/**` |
| `/api/usuario-service/**` | `127.0.0.1:8082/api/**` |
| `/api/producto-service/**` | `127.0.0.1:8083/api/**` |
| `/api/pedido-service/**` | `127.0.0.1:8084/api/**` |
| `/api/metrica-service/**` | `127.0.0.1:8085/api/**` |
| todo lo demás | React (fallback a `index.html`) |

El frontend se compila con `.env.production` (rutas relativas `/api/<servicio>-service/v1`).

## Bases de datos

Una base y un usuario MariaDB por servicio (`deploy/mariadb/crear_bases.sql`).
`usuario-service` comparte `tienda_musica_auth` con `auth-service` (una sola fuente de identidad), cada uno con su propia cuenta.

## Secretos

Cada servicio lee sus variables desde `/etc/tienda-musica/<servicio>-service.env` (permisos 600, `EnvironmentFile` de systemd).
`instalar_en_ec2.sh` los genera con contraseñas aleatorias y un `JWT_SECRET` compartido. **Nunca se suben a Git** (plantillas en `deploy/env/`).
Cada servicio arranca con `--spring.profiles.active=prod` (`application-prod.properties`).

## Desplegar

PowerShell, desde `Tienda_Musica_Backend`:

```powershell
.\deploy\subir_a_ec2.ps1 -Ip IP_PUBLICA -Pem "RUTA\CLAVE.pem"
```

1. Corrige permisos de la `.pem` con `icacls`.
2. `mvnw clean package` de los 5 servicios y `npm run build` del frontend.
3. Copia los JAR, el frontend y `deploy/` a `/tmp` con `scp`.
4. Ejecuta `sudo bash /tmp/deploy/instalar_en_ec2.sh IP_PUBLICA`: MariaDB, Java 21, Nginx, bases, usuarios, `.env`, servicios systemd y Nginx.

Para actualizar después de un cambio se ejecuta el mismo comando: conserva las contraseñas y el secreto JWT.

## Diagnóstico (por SSH)

```bash
systemctl status tienda-auth tienda-usuario tienda-producto tienda-pedido tienda-metrica
sudo journalctl -u tienda-pedido -n 80        # logs de un servicio
sudo nginx -t && sudo systemctl reload nginx
```

## Swagger (túnel SSH, no queda público)

```powershell
ssh -i "RUTA\CLAVE.pem" -L 18083:127.0.0.1:8083 ubuntu@IP_PUBLICA
# luego abrir http://localhost:18083/swagger-ui.html (puerto local distinto para no chocar con el entorno de desarrollo)
```
