package com.example.mscheckout.controller;

import com.example.mscheckout.dto.CheckoutRequestDTO;
import com.example.mscheckout.dto.CheckoutResultDTO;
import com.example.mscheckout.security.jwt.JwtService;
import com.example.mscheckout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Simular usuario autenticado en el SecurityContext
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("juan", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ── POST /api/v1/checkout exitoso ─────────────────────────────────────────

    @Test
    void deberiaRetornarResultadoConfirmadoCuandoCompraEsExitosa() throws Exception {

        CheckoutResultDTO resultado = new CheckoutResultDTO(
                100L, "juan", 1999.98, "CONFIRMADO", "Compra realizada exitosamente"
        );

        when(checkoutService.procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any()))
                .thenReturn(resultado);

        String json = """
                {
                    "items": [
                        { "productoId": 1, "cantidad": 2 }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token-jwt")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(100))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.total").value(1999.98))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.mensaje").value("Compra realizada exitosamente"));

        verify(checkoutService).procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any());
    }

    // ── POST /api/v1/checkout con error de negocio ────────────────────────────

    @Test
    void deberiaRetornar400CuandoCarritoEstaVacio() throws Exception {

        when(checkoutService.procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any()))
                .thenThrow(new RuntimeException("El carrito no puede estar vacío"));

        String json = """
                {
                    "items": []
                }
                """;

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token-jwt")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El carrito no puede estar vacío"));

        verify(checkoutService).procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any());
    }

    // ── POST /api/v1/checkout con stock insuficiente ──────────────────────────

    @Test
    void deberiaRetornar400CuandoStockEsInsuficiente() throws Exception {

        when(checkoutService.procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any()))
                .thenThrow(new RuntimeException("Stock insuficiente para: Laptop Dell"));

        String json = """
                {
                    "items": [
                        { "productoId": 1, "cantidad": 999 }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token-jwt")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock insuficiente para: Laptop Dell"));

        verify(checkoutService).procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any());
    }

    // ── POST /api/v1/checkout con producto no encontrado ─────────────────────

    @Test
    void deberiaRetornar400CuandoProductoNoExiste() throws Exception {

        when(checkoutService.procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any()))
                .thenThrow(new RuntimeException("Producto no encontrado: id=99"));

        String json = """
                {
                    "items": [
                        { "productoId": 99, "cantidad": 1 }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token-jwt")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Producto no encontrado: id=99"));

        verify(checkoutService).procesarCompra(eq("juan"), any(CheckoutRequestDTO.class), any());
    }
}
