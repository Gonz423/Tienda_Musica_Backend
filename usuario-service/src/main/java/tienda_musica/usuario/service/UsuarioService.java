package tienda_musica.usuario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.usuario.dto.ActualizarUsuarioRequest;
import tienda_musica.usuario.dto.UsuarioResponse;
import tienda_musica.usuario.exception.RecursoNoEncontradoException;
import tienda_musica.usuario.model.HistorialAuditoria;
import tienda_musica.usuario.model.Usuario;
import tienda_musica.usuario.repository.HistorialAuditoriaRepository;
import tienda_musica.usuario.repository.UsuarioRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Set<String> ROLES_VALIDOS = Set.of("ADMIN", "VENDEDOR", "CLIENTE");

    private final UsuarioRepository usuarioRepository;
    private final HistorialAuditoriaRepository historialAuditoriaRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, ActualizarUsuarioRequest request, String adminEmail) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        String nuevoRol = request.getRol().toUpperCase().trim();
        if (!ROLES_VALIDOS.contains(nuevoRol)) {
            throw new IllegalArgumentException("Rol inválido: '" + request.getRol() + "'. Roles permitidos: ADMIN, VENDEDOR, CLIENTE");
        }

        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo())
                && usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe otro usuario registrado con el correo: " + request.getCorreo());
        }

        String rolAnterior = usuario.getRol();
        boolean cambioRol = !rolAnterior.equalsIgnoreCase(nuevoRol);

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setRol(nuevoRol);
        if (request.getRut() != null) {
            usuario.setRut(request.getRut());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        Usuario guardado = usuarioRepository.save(usuario);

        if (cambioRol) {
            HistorialAuditoria auditoriaRol = new HistorialAuditoria(
                    guardado.getId(),
                    guardado.getCorreo(),
                    "CAMBIO_ROL",
                    "Cambio de rol de " + rolAnterior + " a " + nuevoRol,
                    adminEmail
            );
            historialAuditoriaRepository.save(auditoriaRol);
        } else {
            HistorialAuditoria auditoriaDatos = new HistorialAuditoria(
                    guardado.getId(),
                    guardado.getCorreo(),
                    "ACTUALIZACION_DATOS",
                    "Actualización de información general del usuario",
                    adminEmail
            );
            historialAuditoriaRepository.save(auditoriaDatos);
        }

        return UsuarioResponse.fromEntity(guardado);
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, Boolean nuevoEstado, String motivo, String adminEmail) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(nuevoEstado);
        Usuario guardado = usuarioRepository.save(usuario);

        String estadoTexto = Boolean.TRUE.equals(nuevoEstado) ? "ACTIVADO" : "DESACTIVADO";
        StringBuilder detalle = new StringBuilder("Cuenta " + estadoTexto + " por administrador " + adminEmail);
        if (motivo != null && !motivo.isBlank()) {
            detalle.append(". Motivo: ").append(motivo.trim());
        }

        HistorialAuditoria auditoria = new HistorialAuditoria(
                guardado.getId(),
                guardado.getCorreo(),
                "CAMBIO_ESTADO",
                detalle.toString(),
                adminEmail
        );
        historialAuditoriaRepository.save(auditoria);

        return UsuarioResponse.fromEntity(guardado);
    }

    @Transactional
    public HistorialAuditoria registrarNotaAuditoria(Long id, String nota, String adminEmail) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (nota == null || nota.isBlank()) {
            throw new IllegalArgumentException("La nota de auditoría no puede estar vacía");
        }

        HistorialAuditoria auditoria = new HistorialAuditoria(
                usuario.getId(),
                usuario.getCorreo(),
                "NOTA_AUDITORIA",
                nota.trim(),
                adminEmail
        );
        return historialAuditoriaRepository.save(auditoria);
    }

    @Transactional(readOnly = true)
    public List<HistorialAuditoria> obtenerHistorialPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }
        return historialAuditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<HistorialAuditoria> obtenerTodoElHistorial() {
        return historialAuditoriaRepository.findAllByOrderByFechaHoraDesc();
    }
}
