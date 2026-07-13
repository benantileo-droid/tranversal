package com.example.msdelivery.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Schema(description = "Entidad que representa una entrega de pedido")
@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la entrega", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull
    @Schema(description = "ID de la reserva asociada", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long reservationId;

    @NotBlank
    @Schema(description = "Dirección de entrega", example = "Av. Providencia 1234, Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccionEntrega;

    @Schema(description = "Fecha de envío", example = "2025-06-05")
    private LocalDate fechaEnvio;

    @Schema(description = "Fecha estimada de entrega", example = "2025-06-07")
    private LocalDate fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Estado de la entrega", example = "PENDIENTE", allowableValues = {"PENDIENTE", "EN_CAMINO", "ENTREGADO", "CANCELADO"})
    private Estado estado = Estado.PENDIENTE;

    public enum Estado { PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO }
}
