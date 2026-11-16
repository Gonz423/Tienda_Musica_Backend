package tienda_musica.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tienda_musica.example.backend.exception.RecursoNoEncontradoException;
import tienda_musica.example.backend.model.Producto;
import tienda_musica.example.backend.repository.ProductoRepository;

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
        buscarPorId(id); // lanza 404 si no existe
        productoRepository.deleteById(id);
    }
}
