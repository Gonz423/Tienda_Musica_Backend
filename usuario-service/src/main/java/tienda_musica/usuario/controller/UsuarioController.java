package tienda_musica.usuario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tienda_musica.usuario.dto.ActualizarUsuarioRequest;
import tienda_musica.usuario.dto.CrearUsuarioRequest;
import tienda_musica.usuario.dto.CambiarEstadoRequest;
import tienda_musica.usuario.dto.NotaAuditoriaRequest;
import tienda_musica.usuario.dto.UsuarioResponse;
import tienda_musica.usuario.model.HistorialAuditoria;
import tienda_musica.usuario.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios, roles, estados y auditoría (Exclusivo para ADMIN)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private String obtenerAdminEmail(Authentication authentication) {
        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            return authentication.getName();
        }
        return "admin@tiendamusica.cl";
    }

    // 1. GET /api/v1/usuarios — Listar todos los usuarios
    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Retorna el listado completo de usuarios registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado o token JWT inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // 6. GET /api/v1/usuarios/historial/todos — Ver toda la auditoría
    @GetMapping("/historial/todos")
    @Operation(summary = "Ver toda la auditoría", description = "Retorna el registro completo de auditoría global del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial global obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<List<HistorialAuditoria>> verTodaLaAuditoria() {
        return ResponseEntity.ok(usuarioService.obtenerTodoElHistorial());
    }

    // 2. GET /api/v1/usuarios/{id} — Detalle de un usuario
    @GetMapping("/{id:[0-9]+}")
    @Operation(summary = "Detalle de un usuario", description = "Retorna la información detallada de un usuario por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // 3. PUT /api/v1/usuarios/{id} — Actualizar datos y rol
    @PutMapping("/{id:[0-9]+}")
    @Operation(summary = "Actualizar datos y rol de usuario", description = "Permite actualizar nombre, correo, datos personales y cambiar el rol (ADMIN, VENDEDOR, CLIENTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o rol no permitido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request, obtenerAdminEmail(authentication)));
    }

    // 4. PATCH /api/v1/usuarios/{id}/estado — Activar / desactivar
    @PatchMapping("/{id:[0-9]+}/estado")
    @Operation(summary = "Activar o desactivar cuenta", description = "Cambia el estado activo/inactivo de la cuenta del usuario o empleado y registra el evento en auditoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request.getActivo(), request.getMotivo(), obtenerAdminEmail(authentication)));
    }

    // 5. GET /api/v1/usuarios/{id}/historial — Ver auditoría del usuario
    @GetMapping("/{id:[0-9]+}/historial")
    @Operation(summary = "Ver auditoría del usuario", description = "Retorna el historial completo de cambios de rol, activación/desactivación y notas registradas para el usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial del usuario obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<List<HistorialAuditoria>> verHistorialUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerHistorialPorUsuario(id));
    }

    // 7. POST /api/v1/usuarios/{id}/historial — Registrar notas de auditoría en el historial de cada usuario
    @PostMapping("/{id:[0-9]+}/historial")
    @Operation(summary = "Registrar nota de auditoría", description = "Registra una observación o nota formal en el historial de auditoría de un usuario específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nota vacía o inválida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<HistorialAuditoria> registrarNota(
            @PathVariable Long id,
            @Valid @RequestBody NotaAuditoriaRequest request,
            Authentication authentication) {
        HistorialAuditoria auditoria = usuarioService.registrarNotaAuditoria(id, request.getNota(), obtenerAdminEmail(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(auditoria);
    }

    // 8. POST /api/v1/usuarios — Crear usuario (ADMIN crea vendedores, otros admins o clientes)
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea una cuenta con contraseña encriptada en BCrypt y el rol indicado (ADMIN, VENDEDOR, CLIENTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, rol no permitido o correo duplicado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody CrearUsuarioRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crearUsuario(request, obtenerAdminEmail(authentication)));
    }

    // 9. DELETE /api/v1/usuarios/{id} — Eliminar usuario
    @DeleteMapping("/{id:[0-9]+}")
    @Operation(summary = "Eliminar usuario", description = "Elimina la cuenta. El historial de auditoría se conserva. Un admin no puede eliminarse a sí mismo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "400", description = "Intento de eliminar la propia cuenta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        usuarioService.eliminarUsuario(id, obtenerAdminEmail(authentication));
        return ResponseEntity.noContent().build();
    }
}
