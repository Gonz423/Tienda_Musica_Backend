package tienda_musica.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tienda_musica.example.backend.exception.RecursoNoEncontradoException;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoPrueba;

    @BeforeEach
    void setUp() {
        productoPrueba = new Producto(
                "TEST01", "Guitarras", "Guitarra Test", "Marca Test", "Modelo Test",
                10, 199990, "Descripcion de prueba", "https://ejemplo.com/foto.jpg"
        );
        productoPrueba.setId(1L);
    }

    @Test
    void listar_retornaTodosLosProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoPrueba));

        List<Producto> resultado = productoService.listar(null, null);

        assertEquals(1, resultado.size());
        assertEquals("TEST01", resultado.get(0).getCodigo());
        verify(productoRepository).findAll();
    }

    @Test
    void buscarPorId_existente_retornaProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.buscarPorId(999L));
    }

    @Test
    void crear_productoValido_guardaYRetorna() {
        when(productoRepository.existsByCodigo("TEST01")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoPrueba);

        Producto creado = productoService.crear(productoPrueba);

        assertNotNull(creado);
        assertEquals("TEST01", creado.getCodigo());
        verify(productoRepository).save(productoPrueba);
    }

    @Test
    void crear_codigoDuplicado_lanzaExcepcion() {
        when(productoRepository.existsByCodigo("TEST01")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crear(productoPrueba);
        });

        assertTrue(ex.getMessage().contains("Ya existe un producto con el código"));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizar_productoExistente_actualizaDatos() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto nuevosDatos = new Producto(
                "TEST01", "Guitarras", "Guitarra Modificada", "Marca Modificada", "Modelo Modificado",
                15, 250000, "Nueva descripcion", "https://ejemplo.com/nueva.jpg"
        );

        Producto actualizado = productoService.actualizar(1L, nuevosDatos);

        assertEquals("Guitarra Modificada", actualizado.getNombre());
        assertEquals(250000, actualizado.getPrecio());
        assertEquals(15, actualizado.getStock());
    }

    @Test
    void eliminar_productoExistente_eliminaCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.eliminar(1L));
        verify(productoRepository).deleteById(1L);
    }
}
