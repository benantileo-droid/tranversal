package com.example.msreport.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "Entidad que representa un reporte generado en el sistema")
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del reporte", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Schema(description = "Tipo de reporte", example = "VENTAS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipoReporte;

    @Schema(description = "Fecha de generación del reporte", example = "2025-06-01T12:00:00")
    private LocalDateTime fechaGeneracion = LocalDateTime.now();

    @Column(length = 2000)
    @Schema(description = "Descripción del contenido del reporte", example = "Reporte de ventas del mes de mayo")
    private String descripcion;

    @NotNull
    @Schema(description = "ID del usuario que generó el reporte", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long generadoPorUserId;
}
