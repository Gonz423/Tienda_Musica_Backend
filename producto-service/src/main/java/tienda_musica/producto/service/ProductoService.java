package tienda_musica.producto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda_musica.producto.dto.MovimientoStockItem;
import tienda_musica.producto.exception.RecursoNoEncontradoException;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.repository.ProductoRepository;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listar(String categoria, String busqueda) {
        if (categoria != null && !categoria.isBlank()) {
            return productoRepository.findByCategoriaIgnoreCase(categoria.trim());
        }
        if (busqueda != null && !busqueda.isBlank()) {
            String q = busqueda.trim();
            return productoRepository.findByNombreContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrModeloContainingIgnoreCase(q, q, q);
        }
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con código: " + codigo));
    }

    public Producto crear(Producto producto) {
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + producto.getCodigo());
        }
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        if (!existente.getCodigo().equals(datos.getCodigo()) && productoRepository.existsByCodigo(datos.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + datos.getCodigo());
        }
        existente.setCodigo(datos.getCodigo());
        existente.setNombre(datos.getNombre());
        existente.setMarca(datos.getMarca());
        existente.setModelo(datos.getModelo());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setDescripcion(datos.getDescripcion());
        existente.setImagenUrl(datos.getImagenUrl());
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        productoRepository.deleteById(id);
    }

    public static final int UMBRAL_STOCK_CRITICO = 3;

    public Producto ajustarStock(Long id, int delta) {
        if (delta == 0) {
            throw new IllegalArgumentException("El ajuste de stock no puede ser 0");
        }
        Producto producto = buscarPorId(id);
        int stockActual = producto.getStock() != null ? producto.getStock() : 0;
        int nuevoStock = stockActual + delta;

        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No se permite stock negativo. Stock actual: " + stockActual + ", ajuste solicitado: " + delta);
        }

        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    public List<Producto> listarStockCritico() {
        return productoRepository.findByStockLessThanEqualOrderByStockAsc(UMBRAL_STOCK_CRITICO);
    }

    /**
     * Descuenta el stock de todos los ítems de un pedido en una sola transacción.
     * Primero valida todo: si un solo producto no tiene stock suficiente no se descuenta nada.
     */
    @Transactional
    public List<Producto> descontarStock(List<MovimientoStockItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto");
        }
        List<Producto> productos = new java.util.ArrayList<>();
        for (MovimientoStockItem item : items) {
            Producto p = buscarPorId(item.getProductoId());
            int stock = p.getStock() != null ? p.getStock() : 0;
            if (stock < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para \"" + p.getNombre()
                        + "\". Disponible: " + stock + ", solicitado: " + item.getCantidad());
            }
            productos.add(p);
        }
        for (int i = 0; i < items.size(); i++) {
            Producto p = productos.get(i);
            p.setStock(p.getStock() - items.get(i).getCantidad());
        }
        return productoRepository.saveAll(productos);
    }

    /** Devuelve stock al inventario (pedido cancelado). */
    @Transactional
    public List<Producto> reponerStock(List<MovimientoStockItem> items) {
        List<Producto> productos = new java.util.ArrayList<>();
        for (MovimientoStockItem item : items) {
            Producto p = buscarPorId(item.getProductoId());
            int stock = p.getStock() != null ? p.getStock() : 0;
            p.setStock(stock + item.getCantidad());
            productos.add(p);
        }
        return productoRepository.saveAll(productos);
    }
}
