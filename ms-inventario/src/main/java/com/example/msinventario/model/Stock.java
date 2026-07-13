package com.example.msinventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Entidad que representa el stock de un producto en inventario")
@Entity
@Table(name = "stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del registro de stock", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "El productoId no puede ser nulo")
    @Column(name = "producto_id", nullable = false, unique = true)
    @Schema(description = "ID del producto asociado", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    @Schema(description = "Cantidad disponible en stock", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @NotNull(message = "El stock mínimo no puede ser nulo")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_min", nullable = false)
    @Schema(description = "Cantidad mínima antes de generar alerta", example = "5")
    private Integer stockMin;
}
