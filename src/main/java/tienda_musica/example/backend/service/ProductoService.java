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
}
