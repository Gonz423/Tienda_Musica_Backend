package tienda_musica.usuario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tienda_musica.usuario.dto.ActualizarUsuarioRequest;
import tienda_musica.usuario.dto.UsuarioResponse;
import tienda_musica.usuario.exception.RecursoNoEncontradoException;
import tienda_musica.usuario.model.HistorialAuditoria;
import tienda_musica.usuario.model.Usuario;
import tienda_musica.usuario.repository.HistorialAuditoriaRepository;
import tienda_musica.usuario.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistorialAuditoriaRepository historialAuditoriaRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEmpleado;

    @BeforeEach
    void setUp() {
        usuarioEmpleado = new Usuario();
        usuarioEmpleado.setId(2L);
        usuarioEmpleado.setNombre("Vendedor Bodega");
        usuarioEmpleado.setCorreo("empleado@tiendamusica.cl");
        usuarioEmpleado.setPassword("Vendedor123!");
        usuarioEmpleado.setRol("VENDEDOR");
        usuarioEmpleado.setRut("22.222.222-2");
        usuarioEmpleado.setTelefono("+56922222222");
        usuarioEmpleado.setDireccion("Av. Secundaria 200");
        usuarioEmpleado.setActivo(true);
    }

    @Test
    @DisplayName("Listar todos los usuarios retorna lista completa de DTOs")
    void listarTodos_retornaListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioEmpleado));

        List<UsuarioResponse> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("empleado@tiendamusica.cl", resultado.get(0).getCorreo());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Buscar usuario por ID existente retorna detalle de usuario")
    void buscarPorId_existente_retornaUsuario() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));

        UsuarioResponse resultado = usuarioService.buscarPorId(2L);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals("Vendedor Bodega", resultado.getNombre());
        verify(usuarioRepository).findById(2L);
    }

    @Test
    @DisplayName("Buscar usuario por ID no existente lanza RecursoNoEncontradoException")
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Actualizar usuario con cambio de rol actualiza rol y registra en historial")
    void actualizarUsuario_cambioDeRol_actualizaYRegistraHistorial() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
                "Vendedor Ascendido",
                "empleado@tiendamusica.cl",
                "ADMIN",
                "22.222.222-2",
                "+56922222222",
                "Av. Secundaria 200",
                true
        );

        UsuarioResponse respuesta = usuarioService.actualizarUsuario(2L, request, "admin@tiendamusica.cl");

        assertEquals("ADMIN", respuesta.getRol());
        assertEquals("Vendedor Ascendido", respuesta.getNombre());

        ArgumentCaptor<HistorialAuditoria> captor = ArgumentCaptor.forClass(HistorialAuditoria.class);
        verify(historialAuditoriaRepository).save(captor.capture());
        HistorialAuditoria auditoriaGuardada = captor.getValue();

        assertEquals("CAMBIO_ROL", auditoriaGuardada.getAccion());
        assertTrue(auditoriaGuardada.getDetalle().contains("VENDEDOR a ADMIN"));
        assertEquals("admin@tiendamusica.cl", auditoriaGuardada.getRealizadoPor());
    }

    @Test
    @DisplayName("Actualizar usuario con rol inválido lanza IllegalArgumentException")
    void actualizarUsuario_rolInvalido_lanzaExcepcion() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));

        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
                "Vendedor Bodega",
                "empleado@tiendamusica.cl",
                "SUPERUSER",
                "22.222.222-2",
                "+56922222222",
                "Av. Secundaria 200",
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizarUsuario(2L, request, "admin@tiendamusica.cl"));

        assertTrue(ex.getMessage().contains("Rol inválido"));
        verify(usuarioRepository, never()).save(any());
        verify(historialAuditoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar usuario con correo duplicado lanza IllegalArgumentException")
    void actualizarUsuario_correoDuplicado_lanzaExcepcion() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));
        when(usuarioRepository.existsByCorreoIgnoreCase("otro@tiendamusica.cl")).thenReturn(true);

        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(
                "Vendedor Bodega",
                "otro@tiendamusica.cl",
                "VENDEDOR",
                "22.222.222-2",
                "+56922222222",
                "Av. Secundaria 200",
                true
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizarUsuario(2L, request, "admin@tiendamusica.cl"));

        assertTrue(ex.getMessage().contains("Ya existe otro usuario registrado con el correo"));
    }

    @Test
    @DisplayName("Activar o desactivar empleado cambia estado y registra entrada en historial")
    void cambiarEstado_desactivarEmpleado_registraHistorial() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse respuesta = usuarioService.cambiarEstado(2L, false, "Fin de contrato", "admin@tiendamusica.cl");

        assertFalse(respuesta.getActivo());

        ArgumentCaptor<HistorialAuditoria> captor = ArgumentCaptor.forClass(HistorialAuditoria.class);
        verify(historialAuditoriaRepository).save(captor.capture());
        HistorialAuditoria auditoriaGuardada = captor.getValue();

        assertEquals("CAMBIO_ESTADO", auditoriaGuardada.getAccion());
        assertTrue(auditoriaGuardada.getDetalle().contains("DESACTIVADO"));
        assertTrue(auditoriaGuardada.getDetalle().contains("Fin de contrato"));
        assertEquals("admin@tiendamusica.cl", auditoriaGuardada.getRealizadoPor());
    }

    @Test
    @DisplayName("Activar cuenta de empleado registra activación en historial")
    void cambiarEstado_activarEmpleado_registraHistorial() {
        usuarioEmpleado.setActivo(false);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse respuesta = usuarioService.cambiarEstado(2L, true, "Reincorporación laboral", "admin@tiendamusica.cl");

        assertTrue(respuesta.getActivo());

        ArgumentCaptor<HistorialAuditoria> captor = ArgumentCaptor.forClass(HistorialAuditoria.class);
        verify(historialAuditoriaRepository).save(captor.capture());
        HistorialAuditoria auditoriaGuardada = captor.getValue();

        assertEquals("CAMBIO_ESTADO", auditoriaGuardada.getAccion());
        assertTrue(auditoriaGuardada.getDetalle().contains("ACTIVADO"));
        assertTrue(auditoriaGuardada.getDetalle().contains("Reincorporación laboral"));
    }

    @Test
    @DisplayName("Registrar nota de auditoría guarda observación en historial")
    void registrarNotaAuditoria_notaValida_guardaEnHistorial() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));
        when(historialAuditoriaRepository.save(any(HistorialAuditoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialAuditoria resultado = usuarioService.registrarNotaAuditoria(2L, "Revisión trimestral de desempeño completada", "admin@tiendamusica.cl");

        assertNotNull(resultado);
        assertEquals("NOTA_AUDITORIA", resultado.getAccion());
        assertEquals("Revisión trimestral de desempeño completada", resultado.getDetalle());
        assertEquals(2L, resultado.getUsuarioId());
        assertEquals("admin@tiendamusica.cl", resultado.getRealizadoPor());
        verify(historialAuditoriaRepository).save(any(HistorialAuditoria.class));
    }

    @Test
    @DisplayName("Registrar nota de auditoría con texto vacío lanza IllegalArgumentException")
    void registrarNotaAuditoria_notaVacia_lanzaExcepcion() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEmpleado));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarNotaAuditoria(2L, "   ", "admin@tiendamusica.cl"));
    }

    @Test
    @DisplayName("Obtener historial por usuario retorna lista ordenada")
    void obtenerHistorialPorUsuario_existente_retornaHistorial() {
        HistorialAuditoria h1 = new HistorialAuditoria(2L, "empleado@tiendamusica.cl", "CAMBIO_ESTADO", "Cuenta activada", "admin@tiendamusica.cl");
        when(usuarioRepository.existsById(2L)).thenReturn(true);
        when(historialAuditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(2L)).thenReturn(List.of(h1));

        List<HistorialAuditoria> resultado = usuarioService.obtenerHistorialPorUsuario(2L);

        assertEquals(1, resultado.size());
        assertEquals("CAMBIO_ESTADO", resultado.get(0).getAccion());
    }

    @Test
    @DisplayName("Obtener historial por usuario inexistente lanza RecursoNoEncontradoException")
    void obtenerHistorialPorUsuario_noExistente_lanzaExcepcion() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerHistorialPorUsuario(999L));
    }

    @Test
    @DisplayName("Obtener toda la auditoría retorna lista completa global")
    void obtenerTodoElHistorial_retornaTodos() {
        HistorialAuditoria h1 = new HistorialAuditoria(1L, "admin@tiendamusica.cl", "CREACION", "Creación", "admin@tiendamusica.cl");
        HistorialAuditoria h2 = new HistorialAuditoria(2L, "empleado@tiendamusica.cl", "CAMBIO_ROL", "Cambio", "admin@tiendamusica.cl");
        when(historialAuditoriaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(h1, h2));

        List<HistorialAuditoria> resultado = usuarioService.obtenerTodoElHistorial();

        assertEquals(2, resultado.size());
        verify(historialAuditoriaRepository).findAllByOrderByFechaHoraDesc();
    }
}
