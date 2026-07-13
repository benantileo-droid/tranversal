package com.example.mspedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Detalle de un ítem dentro de un pedido")
@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del detalle", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @NotNull(message = "El productoId no puede ser nulo")
    @Column(name = "producto_id", nullable = false)
    @Schema(description = "ID del producto", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    @Schema(description = "Cantidad del producto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
    @Column(name = "precio_unit", nullable = false)
    @Schema(description = "Precio unitario del producto", example = "49999.0")
    private Double precioUnit;

    @NotNull(message = "El subtotal no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El subtotal no puede ser negativo")
    @Column(nullable = false)
    @Schema(description = "Subtotal (cantidad × precioUnit)", example = "99998.0")
    private Double subtotal;
}
