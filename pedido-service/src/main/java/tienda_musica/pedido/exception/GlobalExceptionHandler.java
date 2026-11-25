package tienda_musica.pedido.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(RecursoNoEncontradoException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String,Object>> handleBadRequest(RuntimeException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("Validacion fallida");
        return errorResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleForbidden(RuntimeException ex) {
        return errorResponse(HttpStatus.FORBIDDEN, ex.getMessage() != null ? ex.getMessage() : "Acceso denegado");
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAuthorizationDenied(RuntimeException ex) {
        return errorResponse(HttpStatus.FORBIDDEN, "Acceso denegado: permisos insuficientes");
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String,Object>> handleServicioCaido(ServicioNoDisponibleException ex) {
        return errorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneral(Exception ex) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage());
    }

    private ResponseEntity<Map<String,Object>> errorResponse(HttpStatus status, String mensaje) {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
