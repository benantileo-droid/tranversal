package com.example.mspedidos;

import com.example.mspedidos.controller.PedidoController;
import com.example.mspedidos.model.DetallePedido;
import com.example.mspedidos.model.Pedido;
import com.example.mspedidos.security.jwt.JwtService;
import com.example.mspedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void deberiaCrearPedidoCorrectamente() throws Exception {

        // Simulamos usuario autenticado en el SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("juan", null, List.of())
        );

        DetallePedido detalle = new DetallePedido();
        detalle.setId(1L);
        detalle.setProductoId(1L);
        detalle.setCantidad(2);
        detalle.setPrecioUnit(999.99);
        detalle.setSubtotal(1999.98);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsername("juan");
        pedido.setFecha(LocalDateTime.of(2026, 6, 15, 10, 0));
        pedido.setTotal(1999.98);
        pedido.setEstado("CONFIRMADO");
        pedido.setDetalles(List.of(detalle));

        when(pedidoService.crearPedido(eq("juan"), any(), any()))
                .thenReturn(pedido);

        String json = """
                {
                    "items": [
                        {
                            "productoId": 1,
                            "cantidad": 2,
                            "precioUnit": 999.99
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token.jwt.mock")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.total").value(1999.98))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        verify(pedidoService).crearPedido(eq("juan"), any(), any());
    }

    @Test
    void deberiaRetornar400CuandoFallaCreacionPedido() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("juan", null, List.of())
        );

        when(pedidoService.crearPedido(any(), any(), any()))
                .thenThrow(new RuntimeException("El pedido debe tener al menos un item"));

        String json = """
                {
                    "items": []
                }
                """;

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType("application/json")
                        .header("Authorization", "Bearer token.jwt.mock")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El pedido debe tener al menos un item"));
    }

    @Test
    void deberiaRetornarMisPedidos() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("juan", null, List.of())
        );

        Pedido p1 = new Pedido(1L, "juan", LocalDateTime.now(), 500.0, "CONFIRMADO", List.of());
        Pedido p2 = new Pedido(2L, "juan", LocalDateTime.now(), 200.0, "CONFIRMADO", List.of());

        when(pedidoService.listarPorUsuario("juan"))
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/v1/pedidos/mis-pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("juan"))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(pedidoService).listarPorUsuario("juan");
    }

    @Test
    void deberiaRetornarTodosLosPedidos() throws Exception {

        Pedido p1 = new Pedido(1L, "juan", LocalDateTime.now(), 500.0, "CONFIRMADO", List.of());
        Pedido p2 = new Pedido(2L, "maria", LocalDateTime.now(), 300.0, "CONFIRMADO", List.of());

        when(pedidoService.listarTodos())
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].username").value("maria"));

        verify(pedidoService).listarTodos();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayPedidos() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("juan", null, List.of())
        );

        when(pedidoService.listarPorUsuario("juan"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos/mis-pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(pedidoService).listarPorUsuario("juan");
    }
}
