package com.example.mspedidos.dto;

import lombok.Data;

@Data
public class ItemPedidoDTO {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnit;

}
