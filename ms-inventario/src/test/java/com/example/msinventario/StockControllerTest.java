package com.example.msinventario;

import com.example.msinventario.controller.StockController;
import com.example.msinventario.model.Stock;
import com.example.msinventario.security.jwt.JwtService;
import com.example.msinventario.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
@AutoConfigureMockMvc(addFilters = false)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService service;

    @MockitoBean
    private JwtService jwtService;

    // ─── GET /inventario ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarListaDeStocks() throws Exception {

        Stock s1 = new Stock(1L, 10L, "Laptop",  20, 5);
        Stock s2 = new Stock(2L, 11L, "Mouse",   50, 10);

        when(service.listarTodos())
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].cantidad").value(20))
                .andExpect(jsonPath("$[1].nombre").value("Mouse"));

        verify(service).listarTodos();
    }

    // ─── GET /inventario/{productoId} ────────────────────────────────────────

    @Test
    void deberiaRetornarStockPorProductoId() throws Exception {

        Stock stock = new Stock(1L, 10L, "Laptop", 20, 5);

        when(service.obtenerPorProducto(10L))
                .thenReturn(Optional.of(stock));

        mockMvc.perform(get("/api/v1/inventario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(10))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.cantidad").value(20))
                .andExpect(jsonPath("$.stockMin").value(5));

        verify(service).obtenerPorProducto(10L);
    }

    @Test
    void deberiaRetornar404CuandoProductoNoExisteEnInventario() throws Exception {

        when(service.obtenerPorProducto(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/inventario/99"))
                .andExpect(status().isNotFound());

        verify(service).obtenerPorProducto(99L);
    }

    // ─── PUT /inventario/{productoId}/descontar ──────────────────────────────

    @Test
    void deberiaDescontarStockCorrectamente() throws Exception {

        when(service.descontar(10L, 5))
                .thenReturn(true);

        mockMvc.perform(put("/api/v1/inventario/10/descontar")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock descontado correctamente"));

        verify(service).descontar(10L, 5);
    }

    @Test
    void deberiaRetornar400CuandoStockInsuficienteAlDescontar() throws Exception {

        when(service.descontar(10L, 100))
                .thenReturn(false);

        mockMvc.perform(put("/api/v1/inventario/10/descontar")
                        .param("cantidad", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock insuficiente o producto no encontrado"));

        verify(service).descontar(10L, 100);
    }

    // ─── PUT /inventario/{productoId}/reponer ────────────────────────────────

    @Test
    void deberiaReponerStockCorrectamente() throws Exception {

        when(service.reponer(10L, 50))
                .thenReturn(true);

        mockMvc.perform(put("/api/v1/inventario/10/reponer")
                        .param("cantidad", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock repuesto correctamente"));

        verify(service).reponer(10L, 50);
    }

    @Test
    void deberiaRetornar400CuandoProductoNoExisteAlReponer() throws Exception {

        when(service.reponer(99L, 10))
                .thenReturn(false);

        mockMvc.perform(put("/api/v1/inventario/99/reponer")
                        .param("cantidad", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Producto no encontrado en inventario"));

        verify(service).reponer(99L, 10);
    }

    // ─── POST /inventario ────────────────────────────────────────────────────

    @Test
    void deberiaRegistrarNuevoStockCorrectamente() throws Exception {

        Stock stockGuardado = new Stock(1L, 10L, "Laptop", 20, 5);

        when(service.registrar(10L, "Laptop", 20, 5))
                .thenReturn(stockGuardado);

        String json = """
                {
                    "productoId": 10,
                    "nombre": "Laptop",
                    "cantidad": 20,
                    "stockMin": 5
                }
                """;

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(10))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.cantidad").value(20))
                .andExpect(jsonPath("$.stockMin").value(5));

        verify(service).registrar(10L, "Laptop", 20, 5);
    }

    @Test
    void deberiaRegistrarStockConStockMinPorDefecto() throws Exception {

        // Sin stockMin en el body → el controller usa 5 por defecto
        Stock stockGuardado = new Stock(1L, 10L, "Laptop", 20, 5);

        when(service.registrar(10L, "Laptop", 20, 5))
                .thenReturn(stockGuardado);

        String json = """
                {
                    "productoId": 10,
                    "nombre": "Laptop",
                    "cantidad": 20
                }
                """;

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockMin").value(5));

        verify(service).registrar(10L, "Laptop", 20, 5);
    }

    // ─── GET /inventario/alertas ─────────────────────────────────────────────

    @Test
    void deberiaRetornarProductosBajoMinimo() throws Exception {

        Stock bajo1 = new Stock(1L, 10L, "Laptop", 3, 5);
        Stock bajo2 = new Stock(2L, 11L, "Mouse",  5, 5);

        when(service.productosBajoMinimo())
                .thenReturn(List.of(bajo1, bajo2));

        mockMvc.perform(get("/api/v1/inventario/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].cantidad").value(3))
                .andExpect(jsonPath("$[1].nombre").value("Mouse"))
                .andExpect(jsonPath("$[1].cantidad").value(5));

        verify(service).productosBajoMinimo();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayAlertas() throws Exception {

        when(service.productosBajoMinimo())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/inventario/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).productosBajoMinimo();
    }
}
