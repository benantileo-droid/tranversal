package com.example.mspedidos.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequestDTO {
    private List<ItemPedidoDTO> items;
}
