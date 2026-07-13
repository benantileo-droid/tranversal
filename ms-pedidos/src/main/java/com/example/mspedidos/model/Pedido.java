package com.example.mspedidos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Entidad que representa un pedido realizado por un usuario")
@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del pedido", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El username no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Username del usuario que realizó el pedido", example = "juan123")
    private String username;

    @NotNull(message = "La fecha no puede ser nula")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora del pedido", example = "2025-06-01T10:30:00")
    private LocalDateTime fecha;

    @NotNull(message = "El total no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El total no puede ser negativo")
    @Column(nullable = false)
    @Schema(description = "Total del pedido en pesos", example = "150000.0")
    private Double total;

    @NotBlank(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Estado del pedido", example = "PENDIENTE", allowableValues = {"PENDIENTE", "PROCESADO", "CANCELADO"})
    private String estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Schema(description = "Lista de ítems del pedido")
    private List<DetallePedido> detalles;
}
