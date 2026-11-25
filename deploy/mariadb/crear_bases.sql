-- Una base por microservicio y un usuario MariaDB por microservicio (guía 18).
-- usuario-service comparte tienda_musica_auth con auth-service: una sola fuente de identidad,
-- pero cada servicio entra con su propia cuenta.
-- Reemplaza CLAVE_* antes de ejecutar (instalar_en_ec2.sh lo hace con claves aleatorias).
CREATE DATABASE IF NOT EXISTS tienda_musica_auth      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS tienda_musica_productos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS tienda_musica_pedidos   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS tienda_musica_metricas  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'auth_app'@'localhost'      IDENTIFIED BY 'CLAVE_AUTH';
CREATE USER IF NOT EXISTS 'usuarios_app'@'localhost'  IDENTIFIED BY 'CLAVE_USUARIOS';
CREATE USER IF NOT EXISTS 'productos_app'@'localhost' IDENTIFIED BY 'CLAVE_PRODUCTOS';
CREATE USER IF NOT EXISTS 'pedidos_app'@'localhost'   IDENTIFIED BY 'CLAVE_PEDIDOS';
CREATE USER IF NOT EXISTS 'metricas_app'@'localhost'  IDENTIFIED BY 'CLAVE_METRICAS';

GRANT ALL PRIVILEGES ON tienda_musica_auth.*      TO 'auth_app'@'localhost';
GRANT ALL PRIVILEGES ON tienda_musica_auth.*      TO 'usuarios_app'@'localhost';
GRANT ALL PRIVILEGES ON tienda_musica_productos.* TO 'productos_app'@'localhost';
GRANT ALL PRIVILEGES ON tienda_musica_pedidos.*   TO 'pedidos_app'@'localhost';
GRANT ALL PRIVILEGES ON tienda_musica_metricas.*  TO 'metricas_app'@'localhost';
FLUSH PRIVILEGES;
