package tienda_musica.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para ajustar unidades de stock (+ o -)")
public class AjustarStockRequest {

    @NotNull(message = "La cantidad a ajustar es obligatoria")
    @Schema(description = "Cantidad a ajustar: positiva para incrementar (+5) o negativa para decrementar (-2)", example = "5")
    private Integer cantidad;

    @Schema(description = "Operación opcional: 'INCREMENTAR' o 'DECREMENTAR' (si se omite, se usa el signo de cantidad)", example = "INCREMENTAR")
    private String operacion;

    @Schema(description = "Motivo u observación del ajuste (opcional)", example = "Ingreso de bodega / Ajuste manual")
    private String motivo;

    public AjustarStockRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public int obtenerDeltaEfectivo() {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad de ajuste es obligatoria");
        }
        if (operacion != null && !operacion.isBlank()) {
            String op = operacion.trim().toUpperCase();
            if (op.equals("DECREMENTAR") || op.equals("RESTAR")) {
                return -Math.abs(cantidad);
            } else if (op.equals("INCREMENTAR") || op.equals("SUMAR")) {
                return Math.abs(cantidad);
            }
        }
        return cantidad;
    }
}
