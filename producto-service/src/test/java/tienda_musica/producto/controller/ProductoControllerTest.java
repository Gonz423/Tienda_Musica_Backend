package tienda_musica.producto.controller;

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
import tienda_musica.producto.dto.AjustarStockRequest;
import tienda_musica.producto.exception.GlobalExceptionHandler;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.service.ProductoService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UsernamePasswordAuthenticationToken authVendedor;
    private UsernamePasswordAuthenticationToken authAdmin;

    private Producto productoPrueba;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authVendedor = new UsernamePasswordAuthenticationToken(
                "empleado@tiendamusica.cl",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR"))
        );

        authAdmin = new UsernamePasswordAuthenticationToken(
                "admin@tiendamusica.cl",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        productoPrueba = new Producto(
                "TEST01", "Guitarras", "Guitarra Test", "Marca Test", "Modelo Test",
                10, 199990, "Descripcion", "https://ejemplo.com/foto.jpg"
        );
        productoPrueba.setId(1L);
    }

    @Test
    @DisplayName("PATCH /api/v1/productos/{id}/stock incrementa stock (+5) con rol ADMIN")
    void ajustarStock_incremento_retorna200() throws Exception {
        AjustarStockRequest request = new AjustarStockRequest(5);
        Producto productoActualizado = new Producto(
                "TEST01", "Guitarras", "Guitarra Test", "Marca Test", "Modelo Test",
                15, 199990, "Descripcion", "https://ejemplo.com/foto.jpg"
        );
        productoActualizado.setId(1L);

        when(productoService.ajustarStock(eq(1L), eq(5))).thenReturn(productoActualizado);

        mockMvc.perform(patch("/api/v1/productos/1/stock")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    @DisplayName("PATCH /api/v1/productos/{id}/stock decrementa stock (-3) con rol ADMIN")
    void ajustarStock_admin_retorna200() throws Exception {
        AjustarStockRequest request = new AjustarStockRequest(-3);
        Producto productoActualizado = new Producto(
                "TEST01", "Guitarras", "Guitarra Test", "Marca Test", "Modelo Test",
                7, 199990, "Descripcion", "https://ejemplo.com/foto.jpg"
        );
        productoActualizado.setId(1L);

        when(productoService.ajustarStock(eq(1L), eq(-3))).thenReturn(productoActualizado);

        mockMvc.perform(patch("/api/v1/productos/1/stock")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(7));
    }

    @Test
    @DisplayName("PATCH /api/v1/productos/{id}/stock con stock negativo lanza 400 Bad Request")
    void ajustarStock_stockNegativo_retorna400() throws Exception {
        AjustarStockRequest request = new AjustarStockRequest(-20);

        when(productoService.ajustarStock(eq(1L), eq(-20)))
                .thenThrow(new IllegalArgumentException("No se permite stock negativo. Stock actual: 10, ajuste solicitado: -20"));

        mockMvc.perform(patch("/api/v1/productos/1/stock")
                        .principal(authAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se permite stock negativo. Stock actual: 10, ajuste solicitado: -20"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/stock-critico retorna 200 y productos con stock <= 3")
    void listarStockCritico_retorna200() throws Exception {
        Producto critico = new Producto(
                "CRIT01", "Baterías", "Batería Test", "Marca B", "Modelo B",
                2, 450000, "Stock bajo", "https://ejemplo.com/bateria.jpg"
        );
        critico.setId(2L);

        when(productoService.listarStockCritico()).thenReturn(List.of(critico));

        mockMvc.perform(get("/api/v1/productos/stock-critico").principal(authVendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CRIT01"))
                .andExpect(jsonPath("$[0].stock").value(2))
                .andExpect(jsonPath("$[0].stockCritico").value(true));
    }
}
