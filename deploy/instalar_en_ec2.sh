#!/usr/bin/env bash
# Instala Tienda Música en una EC2 Ubuntu siguiendo el esquema de la guía 18:
# MariaDB (una base y un usuario por servicio) + 5 servicios systemd + React publicado con Nginx.
#
# Uso (en la EC2):  sudo bash /tmp/deploy/instalar_en_ec2.sh <IP_PUBLICA>
# Espera encontrar en /tmp:  auth-service.jar ... metrica-service.jar  y  tienda-frontend.tar.gz
# Es re-ejecutable: si los .env ya existen, conserva las contraseñas y el secreto JWT.
set -euo pipefail

IP_PUBLICA="${1:?Indica la IP pública: sudo bash instalar_en_ec2.sh 1.2.3.4}"
DIR="$(cd "$(dirname "$0")" && pwd)"
SERVICIOS=(auth usuario producto pedido metrica)
ENV_DIR=/etc/tienda-musica
APP_DIR=/opt/tienda-musica
WEB_DIR=/var/www/tienda-musica

paso() { echo; echo "==> $*"; }

paso "1/7 Paquetes: MariaDB, Java 21, Nginx"
export DEBIAN_FRONTEND=noninteractive
apt-get update -y
apt-get install -y mariadb-server openjdk-21-jre-headless nginx curl openssl
systemctl enable --now mariadb nginx

paso "2/7 Memoria swap (5 procesos Java)"
RAM_MB=$(free -m | awk '/^Mem:/{print $2}')
if [ "$RAM_MB" -lt 3500 ] && ! swapon --show | grep -q '/swapfile'; then
  fallocate -l 2G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
  grep -q '/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
  echo "Swap de 2 GB creada (RAM detectada: ${RAM_MB} MB)"
else
  echo "Sin cambios (RAM: ${RAM_MB} MB)"
fi

paso "3/7 Usuario de sistema y carpetas"
id tiendamusica >/dev/null 2>&1 || useradd --system --no-create-home --shell /usr/sbin/nologin tiendamusica
mkdir -p "$ENV_DIR" "$APP_DIR" "$WEB_DIR"
chmod 750 "$ENV_DIR"

paso "4/7 Bases de datos, usuarios MariaDB y archivos .env"
if [ ! -f "$ENV_DIR/auth-service.env" ]; then
  clave() { openssl rand -hex 16; }
  C_AUTH=$(clave); C_USU=$(clave); C_PROD=$(clave); C_PED=$(clave); C_MET=$(clave)
  JWT=$(openssl rand -base64 48 | tr -d '\n')

  sed -e "s/CLAVE_AUTH/$C_AUTH/" -e "s/CLAVE_USUARIOS/$C_USU/" -e "s/CLAVE_PRODUCTOS/$C_PROD/" \
      -e "s/CLAVE_PEDIDOS/$C_PED/" -e "s/CLAVE_METRICAS/$C_MET/" \
      "$DIR/mariadb/crear_bases.sql" | mariadb

  escribir_env() { # servicio prefijo base usuario clave [extra]
    local f="$ENV_DIR/$1-service.env"
    {
      echo "$2_DB_URL=jdbc:mysql://127.0.0.1:3306/$3?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
      echo "$2_DB_USER=$4"
      echo "$2_DB_PASSWORD=$5"
      echo "JWT_SECRET=$JWT"
      echo "APP_CORS_ALLOWED_ORIGINS=http://$IP_PUBLICA"
      if [ -n "${6:-}" ]; then printf '%b\n' "$6"; fi
    } > "$f"
    chmod 600 "$f"
  }
  escribir_env auth     AUTH      tienda_musica_auth      auth_app      "$C_AUTH"
  escribir_env usuario  USUARIOS  tienda_musica_auth      usuarios_app  "$C_USU"
  escribir_env producto PRODUCTOS tienda_musica_productos productos_app "$C_PROD"
  escribir_env pedido   PEDIDOS   tienda_musica_pedidos   pedidos_app   "$C_PED" \
    "PRODUCTO_URL=http://127.0.0.1:8083\nMETRICA_URL=http://127.0.0.1:8085"
  escribir_env metrica  METRICAS  tienda_musica_metricas  metricas_app  "$C_MET" \
    "PEDIDO_URL=http://127.0.0.1:8084\nPRODUCTO_URL=http://127.0.0.1:8083"
  echo "Bases, usuarios y .env creados con claves aleatorias (solo existen en esta EC2)."
else
  # Conserva claves; solo actualiza el origen CORS por si cambió la IP pública
  sed -i "s#^APP_CORS_ALLOWED_ORIGINS=.*#APP_CORS_ALLOWED_ORIGINS=http://$IP_PUBLICA#" "$ENV_DIR"/*-service.env
  echo ".env existentes conservados (CORS actualizado a http://$IP_PUBLICA)."
fi

paso "5/7 JARs y servicios systemd"
for s in "${SERVICIOS[@]}"; do
  if [ -f "/tmp/$s-service.jar" ]; then
    install -o tiendamusica -g tiendamusica -m 640 "/tmp/$s-service.jar" "$APP_DIR/$s-service.jar"
  fi
  [ -f "$APP_DIR/$s-service.jar" ] || { echo "Falta $APP_DIR/$s-service.jar"; exit 1; }
  install -m 644 "$DIR/systemd/tienda-$s.service" "/etc/systemd/system/tienda-$s.service"
done
systemctl daemon-reload
declare -A PUERTO=([auth]=8081 [usuario]=8082 [producto]=8083 [pedido]=8084 [metrica]=8085)
esperar() { # servicio -> espera hasta 4 min a que responda
  for _ in $(seq 1 80); do
    if curl -s -o /dev/null "http://127.0.0.1:${PUERTO[$1]}/v3/api-docs"; then return 0; fi
    sleep 3
  done
  return 1
}
# Uno a uno: en una instancia pequeña arrancar los 5 Java a la vez satura la CPU
for s in "${SERVICIOS[@]}"; do
  systemctl enable "tienda-$s" >/dev/null 2>&1
  systemctl restart "tienda-$s"
  if esperar "$s"; then echo "  $s-service iniciado"; else echo "  $s-service tarda en iniciar, se continúa"; fi
done

paso "6/7 Frontend React y Nginx"
if [ -f /tmp/tienda-frontend.tar.gz ]; then
  rm -rf "${WEB_DIR:?}"/*
  tar -xzf /tmp/tienda-frontend.tar.gz -C "$WEB_DIR"
  chown -R www-data:www-data "$WEB_DIR"
fi
install -m 644 "$DIR/nginx/tienda-musica.conf" /etc/nginx/sites-available/tienda-musica
ln -sf /etc/nginx/sites-available/tienda-musica /etc/nginx/sites-enabled/tienda-musica
rm -f /etc/nginx/sites-enabled/default
nginx -t
systemctl reload nginx

paso "7/7 Esperando a que los 5 servicios respondan"
for s in "${SERVICIOS[@]}"; do
  if esperar "$s"; then echo "  [OK] $s-service en 127.0.0.1:${PUERTO[$s]}"
  else echo "  [FALLA] $s-service no responde. Revisa: sudo journalctl -u tienda-$s -n 80"; fi
done

echo
echo "Listo: http://$IP_PUBLICA"
