package com.example.productos;

import com.example.productos.controller.ProductoController;
import com.example.productos.model.Producto;
import com.example.productos.security.jwt.JwtService;
import com.example.productos.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayProductos() throws Exception {

        when(service.listar())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/productos")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).listar();
    }

    @Test
    void deberiaRetornar400CuandoProductoNoExisteAlDescontar() throws Exception {

        when(service.descontarStock(99L, 1))
                .thenReturn(false);

        mockMvc.perform(put("/api/v1/productos/99/descontar-stock")
                        .param("cantidad", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock insuficiente o producto no encontrado"));

        verify(service).descontarStock(99L, 1);
    }

    @Test
    void deberiaRetornarProductoConStockCero() throws Exception {

        Producto producto = new Producto(1L, "Agotado", 50.0, 0);

        when(service.obtener(1L))
                .thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/v1/productos/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Agotado"))
                .andExpect(jsonPath("$.stock").value(0));

        verify(service).obtener(1L);
    }

    @Test
    void deberiaDescontarStockDejaExactamenteCero() throws Exception {

        when(service.descontarStock(1L, 10))
                .thenReturn(true);

        mockMvc.perform(put("/api/v1/productos/1/descontar-stock")
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock actualizado"));

        verify(service).descontarStock(1L, 10);
    }
}
