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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deberiaRetornarListaDeProductos() throws Exception {

        Producto p1 = new Producto(1L, "Laptop", 999.99, 10);
        Producto p2 = new Producto(2L, "Mouse", 29.99, 50);

        when(service.listar())
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/v1/productos")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].precio").value(999.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Mouse"));

        verify(service).listar();
    }

    @Test
    void deberiaRetornarProductoPorId() throws Exception {

        Producto producto = new Producto(1L, "Laptop", 999.99, 10);

        when(service.obtener(1L))
                .thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/v1/productos/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(999.99))
                .andExpect(jsonPath("$.stock").value(10));

        verify(service).obtener(1L);
    }

    @Test
    void deberiaRetornar404CuandoProductoNoExiste() throws Exception {

        when(service.obtener(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/productos/99")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(service).obtener(99L);
    }

    @Test
    void deberiaDescontarStockCorrectamente() throws Exception {

        when(service.descontarStock(1L, 3))
                .thenReturn(true);

        mockMvc.perform(put("/api/v1/productos/1/descontar-stock")
                        .param("cantidad", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock actualizado"));

        verify(service).descontarStock(1L, 3);
    }

    @Test
    void deberiaRetornar400CuandoStockInsuficiente() throws Exception {

        when(service.descontarStock(1L, 100))
                .thenReturn(false);

        mockMvc.perform(put("/api/v1/productos/1/descontar-stock")
                        .param("cantidad", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock insuficiente o producto no encontrado"));

        verify(service).descontarStock(1L, 100);
    }
}
