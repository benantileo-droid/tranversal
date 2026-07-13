package com.example.mscheckout.controller;

import com.example.mscheckout.dto.CheckoutRequestDTO;
import com.example.mscheckout.dto.CheckoutResultDTO;
import com.example.mscheckout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Checkout", description = "Procesamiento de compras y checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @Operation(summary = "Procesar checkout", description = "Procesa la compra del carrito del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compra procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al procesar la compra"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
            CheckoutResultDTO result = checkoutService.procesarCompra(username, request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
