package tienda_musica.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tienda_musica.usuario.dto.ActualizarUsuarioRequest;
import tienda_musica.usuario.dto.CambiarEstadoRequest;
import tienda_musica.usuario.dto.NotaAuditoriaRequest;
import tienda_musica.usuario.dto.UsuarioResponse;
import tienda_musica.usuario.exception.GlobalExceptionHandler;
import tienda_musica.usuario.exception.RecursoNoEncontradoException;
import tienda_musica.usuario.model.HistorialAuditoria;
import tienda_musica.usuario.service.UsuarioService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UsernamePasswordAuthenticationToken authAdmin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authAdmin = new UsernamePasswordAuthenticationToken(
                "admin@tiendamusica.cl",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("GET /api/usuarios retorna 200 y listado de usuarios")
    void listarTodos_retorna200() throws Exception {
        UsuarioResponse resp = UsuarioResponse.builder()
                .id(1L)
                .nombre("Admin")
                .correo("admin@tiendamusica.cl")
                .rol("ADMIN")
                .activo(true)
                .build();

        when(usuarioService.listarTodos()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/usuarios").principal(authAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("admin@tiendamusica.cl"))
                .andExpect(jsonPath("$[0].rol").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} retorna 200 con detalle del usuario")
    void obtenerPorId_retorna200() throws Exception {
        UsuarioResponse resp = UsuarioResponse.builder()
                .id(2L)
                .nombre("Empleado")
                .correo("empleado@tiendamusica.cl")
                .rol("VENDEDOR")
                .activo(true)
                .build();

        when(usuarioService.buscarPorId(2L)).thenReturn(resp);

        mockMvc.perform(get("/api/usuarios/2").principal(authAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nombre").value("Empleado"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} no encontrado retorna 404")
    void obtenerPorId_noEncontrado_retorna404() throws Exception {
        when(usuarioService.buscarPorId(999L)).thenThrow(new RecursoNoEncontradoException("Usuario no encontrado con ID: 999"));

        mockMvc.perform(get("/api/usuarios/999").principal(authAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado con ID: 999"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} actualiza datos y rol correctamente")
    void actualizar_retorna200() throws Exception {
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "Empleado Actualizado",
                "empleado@tiendamusica.cl",
                "ADMIN",
                "22.222.222-2",
                "+56922222222",
                "Dirección 123",
                true
        );

        UsuarioResponse resp = UsuarioResponse.builder()
                .id(2L)
                .nombre("Empleado Actualizado")
                .correo("empleado@tiendamusica.cl")
                .rol("ADMIN")
                .activo(true)
                .build();

        when(usuarioService.actualizarUsuario(eq(2L), any(ActualizarUsuarioRequest.class), anyString())).thenReturn(resp);

        mockMvc.perform(put("/api/usuarios/2")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.nombre").value("Empleado Actualizado"));
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/estado activa o desactiva la cuenta")
    void cambiarEstado_retorna200() throws Exception {
        CambiarEstadoRequest req = new CambiarEstadoRequest(false, "Desactivado por término de contrato");

        UsuarioResponse resp = UsuarioResponse.builder()
                .id(2L)
                .nombre("Empleado")
                .correo("empleado@tiendamusica.cl")
                .rol("VENDEDOR")
                .activo(false)
                .build();

        when(usuarioService.cambiarEstado(eq(2L), eq(false), anyString(), anyString())).thenReturn(resp);

        mockMvc.perform(patch("/api/usuarios/2/estado")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id}/historial retorna auditoría de un usuario")
    void verHistorialUsuario_retorna200() throws Exception {
        HistorialAuditoria h = new HistorialAuditoria(2L, "empleado@tiendamusica.cl", "CAMBIO_ESTADO", "Cuenta desactivada", "admin@tiendamusica.cl");

        when(usuarioService.obtenerHistorialPorUsuario(2L)).thenReturn(List.of(h));

        mockMvc.perform(get("/api/usuarios/2/historial").principal(authAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accion").value("CAMBIO_ESTADO"))
                .andExpect(jsonPath("$[0].detalle").value("Cuenta desactivada"));
    }

    @Test
    @DisplayName("GET /api/usuarios/historial/todos retorna todo el registro de auditoría")
    void verTodaLaAuditoria_retorna200() throws Exception {
        HistorialAuditoria h1 = new HistorialAuditoria(1L, "admin@tiendamusica.cl", "CREACION", "Creación", "admin@tiendamusica.cl");
        HistorialAuditoria h2 = new HistorialAuditoria(2L, "empleado@tiendamusica.cl", "CAMBIO_ROL", "Cambio rol", "admin@tiendamusica.cl");

        when(usuarioService.obtenerTodoElHistorial()).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/usuarios/historial/todos").principal(authAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/usuarios/{id}/historial registra una nota de auditoría")
    void registrarNota_retorna201() throws Exception {
        NotaAuditoriaRequest req = new NotaAuditoriaRequest("Nota de supervisión técnica");
        HistorialAuditoria h = new HistorialAuditoria(2L, "empleado@tiendamusica.cl", "NOTA_AUDITORIA", "Nota de supervisión técnica", "admin@tiendamusica.cl");

        when(usuarioService.registrarNotaAuditoria(eq(2L), eq("Nota de supervisión técnica"), anyString())).thenReturn(h);

        mockMvc.perform(post("/api/usuarios/2/historial")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accion").value("NOTA_AUDITORIA"))
                .andExpect(jsonPath("$.detalle").value("Nota de supervisión técnica"));
    }
}
