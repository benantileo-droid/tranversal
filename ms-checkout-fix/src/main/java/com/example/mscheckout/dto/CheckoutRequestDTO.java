package com.example.mscheckout.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequestDTO {
    private List<ItemCheckoutDTO> items;
}
