package tienda_musica.usuario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    private String usuarioCorreo;

    @Column(nullable = false)
    private String accion; // CAMBIO_ROL, CAMBIO_ESTADO, ACTUALIZACION_DATOS, NOTA_AUDITORIA

    @Column(columnDefinition = "TEXT")
    private String detalle;

    private String realizadoPor; // correo del administrador responsable

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @PrePersist
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }

    public HistorialAuditoria(Long usuarioId, String usuarioCorreo, String accion, String detalle, String realizadoPor) {
        this.usuarioId = usuarioId;
        this.usuarioCorreo = usuarioCorreo;
        this.accion = accion;
        this.detalle = detalle;
        this.realizadoPor = realizadoPor;
        this.fechaHora = LocalDateTime.now();
    }
}
