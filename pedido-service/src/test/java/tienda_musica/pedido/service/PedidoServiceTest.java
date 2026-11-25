package tienda_musica.pedido.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import tienda_musica.pedido.client.MetricaCliente;
import tienda_musica.pedido.client.ProductoCliente;
import tienda_musica.pedido.client.ProductoCliente.ProductoRemoto;
import tienda_musica.pedido.dto.CrearPedidoRequest;
import tienda_musica.pedido.dto.ItemPedidoRequest;
import tienda_musica.pedido.model.DetallePedido;
import tienda_musica.pedido.model.EstadoPedido;
import tienda_musica.pedido.model.Pedido;
import tienda_musica.pedido.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProductoCliente productoCliente;
    @Mock
    private MetricaCliente metricaCliente;

    @InjectMocks
    private PedidoService pedidoService;

    private static ItemPedidoRequest item(long id, int cantidad) {
        ItemPedidoRequest i = new ItemPedidoRequest();
        i.setProductoId(id);
        i.setCantidad(cantidad);
        return i;
    }

    private static CrearPedidoRequest request(ItemPedidoRequest... items) {
        CrearPedidoRequest r = new CrearPedidoRequest();
        r.setDireccionEnvio("Av. Siempre Viva 123");
        r.setItems(List.of(items));
        return r;
    }

    private static Pedido pedidoEnEstado(EstadoPedido estado) {
        Pedido p = Pedido.builder().id(1L).clienteId("cliente@tiendamusica.cl").estado(estado)
                .total(BigDecimal.valueOf(1000)).direccionEnvio("x").detalles(new ArrayList<>()).build();
        p.getDetalles().add(DetallePedido.builder().productoId(5L).nombreProducto("Guitarra").cantidad(2)
                .precioUnitario(BigDecimal.valueOf(500)).subtotal(BigDecimal.valueOf(1000)).build());
        return p;
    }

    @Test
    void crearPedido_usaPrecioDelCatalogoYClienteDelToken() {
        when(productoCliente.obtenerProducto(1L)).thenReturn(new ProductoRemoto(1L, "AC001", "Guitarra", 150000, 10));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        // el mismo producto dos veces en el carrito se agrupa en una sola línea
        Pedido pedido = pedidoService.crearPedido(request(item(1L, 1), item(1L, 2)), "cliente@tiendamusica.cl");

        assertThat(pedido.getClienteId()).isEqualTo("cliente@tiendamusica.cl");
        assertThat(pedido.getDetalles()).hasSize(1);
        assertThat(pedido.getDetalles().get(0).getCantidad()).isEqualTo(3);
        assertThat(pedido.getTotal()).isEqualByComparingTo("450000");
        verify(productoCliente).descontarStock(eq(List.of(new ProductoCliente.MovimientoStock(1L, 3))));
    }

    @Test
    void crearPedido_sinStockSuficiente_lanzaErrorYNoGuarda() {
        when(productoCliente.obtenerProducto(1L)).thenReturn(new ProductoRemoto(1L, "AC001", "Guitarra", 150000, 1));

        assertThatThrownBy(() -> pedidoService.crearPedido(request(item(1L, 5)), "c@x.cl"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");
        verify(pedidoRepository, never()).save(any());
        verify(productoCliente, never()).descontarStock(anyList());
    }

    @Test
    void actualizarEstado_transicionValida() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.PENDIENTE)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido p = pedidoService.actualizarEstado(1L, "en_preparacion", "Bearer t");

        assertThat(p.getEstado()).isEqualTo(EstadoPedido.EN_PREPARACION);
    }

    @Test
    void actualizarEstado_saltoNoPermitido_lanzaError() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.PENDIENTE)));

        assertThatThrownBy(() -> pedidoService.actualizarEstado(1L, "ENTREGADO", "Bearer t"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transicion no permitida");
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_cancelar_reponeStock() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.EN_PREPARACION)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.actualizarEstado(1L, "CANCELADO", "Bearer t");

        verify(productoCliente).reponerStock(List.of(new ProductoCliente.MovimientoStock(5L, 2)));
    }

    @Test
    void actualizarEstado_entregado_registraVentaEnMetricas() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.ENVIADO)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.actualizarEstado(1L, "ENTREGADO", "Bearer admin");

        verify(metricaCliente).registrarVenta(any(Pedido.class), eq("Bearer admin"));
    }

    @Test
    void buscarPorIdAutorizado_clienteNoPuedeVerPedidoAjeno() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.PENDIENTE)));

        assertThatThrownBy(() -> pedidoService.buscarPorIdAutorizado(1L, "otro@x.cl", false))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(pedidoService.buscarPorIdAutorizado(1L, "cliente@tiendamusica.cl", false)).isNotNull();
        assertThat(pedidoService.buscarPorIdAutorizado(1L, "vendedor@x.cl", true)).isNotNull();
    }
}
