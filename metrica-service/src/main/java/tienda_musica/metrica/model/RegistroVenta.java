package tienda_musica.metrica.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Snapshot por pedido entregado para calcular KPIs.
 * Se registra cuando un pedido llega a estado ENTREGADO.
 */
@Entity
@Table(name = "registros_venta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(name = "email_cliente", nullable = false)
    private String emailCliente;

    @Column(name = "total_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalVenta;

    @Column(name = "descuento_aplicado", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(name = "cantidad_items", nullable = false)
    private int cantidadItems;

    // producto destacado del pedido (el de mayor cantidad)
    @Column(name = "producto_destacado")
    private String productoDestacado;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (fechaVenta == null) {
            fechaVenta = LocalDate.now();
        }
    }
}
