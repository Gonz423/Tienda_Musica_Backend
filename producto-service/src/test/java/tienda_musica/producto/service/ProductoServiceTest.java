package tienda_musica.producto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tienda_musica.producto.exception.RecursoNoEncontradoException;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.repository.ProductoRepository;

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
    @DisplayName("Listar productos retorna lista completa")
    void listar_retornaTodosLosProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoPrueba));

        List<Producto> resultado = productoService.listar(null, null);

        assertEquals(1, resultado.size());
        assertEquals("TEST01", resultado.get(0).getCodigo());
        verify(productoRepository).findAll();
    }

    @Test
    @DisplayName("Buscar producto por ID existente retorna producto")
    void buscarPorId_existente_retornaProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Buscar producto por ID no existente lanza excepción")
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Crear producto con código válido guarda y retorna")
    void crear_productoValido_guardaYRetorna() {
        when(productoRepository.existsByCodigo("TEST01")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoPrueba);

        Producto creado = productoService.crear(productoPrueba);

        assertNotNull(creado);
        assertEquals("TEST01", creado.getCodigo());
        verify(productoRepository).save(productoPrueba);
    }

    @Test
    @DisplayName("Crear producto con código duplicado lanza excepción")
    void crear_codigoDuplicado_lanzaExcepcion() {
        when(productoRepository.existsByCodigo("TEST01")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crear(productoPrueba);
        });

        assertTrue(ex.getMessage().contains("Ya existe un producto con el código"));
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar producto existente actualiza datos")
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
    @DisplayName("Eliminar producto existente elimina correctamente")
    void eliminar_productoExistente_eliminaCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.eliminar(1L));
        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Ajustar stock incrementando unidades actualiza correctamente")
    void ajustarStock_incrementarStock_actualizaCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.ajustarStock(1L, 5);

        assertNotNull(resultado);
        assertEquals(15, resultado.getStock()); // 10 + 5 = 15
        verify(productoRepository).save(productoPrueba);
    }

    @Test
    @DisplayName("Ajustar stock decrementando unidades actualiza correctamente")
    void ajustarStock_decrementarStock_actualizaCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.ajustarStock(1L, -4);

        assertNotNull(resultado);
        assertEquals(6, resultado.getStock()); // 10 - 4 = 6
        verify(productoRepository).save(productoPrueba);
    }

    @Test
    @DisplayName("Ajustar stock con resultado negativo lanza IllegalArgumentException")
    void ajustarStock_stockNegativo_lanzaIllegalArgumentException() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoPrueba));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.ajustarStock(1L, -15); // 10 - 15 = -5
        });

        assertTrue(ex.getMessage().contains("No se permite stock negativo"));
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ajustar stock con delta cero lanza IllegalArgumentException")
    void ajustarStock_deltaCero_lanzaIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.ajustarStock(1L, 0);
        });

        assertTrue(ex.getMessage().contains("no puede ser 0"));
    }

    @Test
    @DisplayName("Listar stock crítico retorna solo productos con stock <= 3")
    void listarStockCritico_retornaSoloProductosConStockMenorOIgualA3() {
        Producto critico = new Producto(
                "CRIT01", "Baterías", "Batería Test", "Marca B", "Modelo B",
                2, 450000, "Stock bajo", "https://ejemplo.com/bateria.jpg"
        );
        critico.setId(2L);

        when(productoRepository.findByStockLessThanEqualOrderByStockAsc(3)).thenReturn(List.of(critico));

        List<Producto> criticos = productoService.listarStockCritico();

        assertEquals(1, criticos.size());
        assertEquals("CRIT01", criticos.get(0).getCodigo());
        assertTrue(criticos.get(0).getStock() <= 3);
        assertTrue(criticos.get(0).isStockCritico());
        verify(productoRepository).findByStockLessThanEqualOrderByStockAsc(3);
    }
}
