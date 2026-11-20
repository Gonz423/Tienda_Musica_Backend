package tienda_musica.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tienda_musica.usuario.model.HistorialAuditoria;

import java.util.List;

@Repository
public interface HistorialAuditoriaRepository extends JpaRepository<HistorialAuditoria, Long> {
    List<HistorialAuditoria> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);
    List<HistorialAuditoria> findAllByOrderByFechaHoraDesc();
}
