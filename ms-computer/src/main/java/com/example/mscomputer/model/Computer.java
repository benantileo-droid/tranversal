package com.example.mscomputer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Schema(description = "Entidad que representa un computador de la tienda")
@Entity
@Table(name = "computers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del computador", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Schema(description = "Nombre del equipo", example = "Dell XPS 15", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank
    @Schema(description = "Marca del equipo", example = "Dell", requiredMode = Schema.RequiredMode.REQUIRED)
    private String marca;

    @NotBlank
    @Schema(description = "Modelo del equipo", example = "XPS 15 9530", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelo;

    @NotBlank
    @Schema(description = "Procesador del equipo", example = "Intel Core i7-13700H", requiredMode = Schema.RequiredMode.REQUIRED)
    private String procesador;

    @NotBlank
    @Schema(description = "Memoria RAM", example = "16GB DDR5", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ram;

    @NotBlank
    @Schema(description = "Almacenamiento", example = "512GB SSD NVMe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String almacenamiento;

    @NotNull
    @DecimalMin("0.0")
    @Schema(description = "Precio del equipo", example = "1299990")
    private BigDecimal precio;

    @Column(length = 1000)
    @Schema(description = "Descripción del equipo", example = "Laptop de alto rendimiento para profesionales")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Disponibilidad del equipo", example = "DISPONIBLE", allowableValues = {"DISPONIBLE", "NO_DISPONIBLE"})
    private Estado estado = Estado.DISPONIBLE;

    public enum Estado { DISPONIBLE, NO_DISPONIBLE }
}
