package com.example.productos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Entidad que representa un producto de la tienda")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Schema(description = "Precio del producto", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad disponible en stock", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;
}
