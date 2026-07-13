package com.example.mscheckout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResultDTO {
    private Long pedidoId;
    private String username;
    private Double total;
    private String estado;
    private String mensaje;
}
