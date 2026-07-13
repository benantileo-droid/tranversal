package com.example.msreview.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Schema(description = "Entidad que representa una reseña de un computador")
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reseña", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull
    @Schema(description = "ID del usuario que hace la reseña", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull
    @Schema(description = "ID del computador reseñado", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long computerId;

    @Min(1) @Max(5)
    @Schema(description = "Puntuación del 1 al 5", example = "4", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private int puntuacion;

    @NotBlank
    @Column(length = 1000)
    @Schema(description = "Comentario de la reseña", example = "Excelente computador, muy rápido", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comentario;

    @Schema(description = "Fecha de la reseña", example = "2025-06-01")
    private LocalDate fecha = LocalDate.now();
}
