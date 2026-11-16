package tienda_musica.metrica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tienda_musica.metrica.model.RegistroVenta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RegistroVentaRepository extends JpaRepository<RegistroVenta, Long> {

    List<RegistroVenta> findByFechaVentaBetweenOrderByFechaVentaDesc(LocalDate inicio, LocalDate fin);

    @Query("SELECT SUM(r.totalVenta) FROM RegistroVenta r WHERE r.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal sumTotalVentaByFechaVentaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(r) FROM RegistroVenta r WHERE r.fechaVenta BETWEEN :inicio AND :fin")
    long countPedidosByFechaVentaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(r.cantidadItems) FROM RegistroVenta r WHERE r.fechaVenta BETWEEN :inicio AND :fin")
    Long sumCantidadItemsByFechaVentaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // Producto mas vendido: el que aparece mas veces como destacado
    @Query("SELECT r.productoDestacado FROM RegistroVenta r WHERE r.productoDestacado IS NOT NULL " +
           "GROUP BY r.productoDestacado ORDER BY COUNT(r.productoDestacado) DESC")
    List<String> findTopProductos();

    boolean existsByPedidoId(Long pedidoId);

    long countByFechaVentaBetween(LocalDate inicio, LocalDate fin);
}
