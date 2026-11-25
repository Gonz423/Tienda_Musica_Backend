package tienda_musica.pedido.exception;

/** Otro microservicio (producto o métricas) no respondió. Se traduce a HTTP 503. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
