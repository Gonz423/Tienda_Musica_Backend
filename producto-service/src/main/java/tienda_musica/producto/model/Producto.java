package tienda_musica.producto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false)
    private String modelo;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Integer precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Column(length = 1000)
    private String descripcion;

    private String imagenUrl;

    public Producto(String codigo, String categoria, String nombre, String marca, String modelo, Integer stock, Integer precio, String descripcion, String imagenUrl) {
        this.codigo = codigo;
        this.categoria = categoria;
        this.nombre = nombre;
        this.marca = marca;
        this.modelo = modelo;
        this.stock = stock;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }
}
