package com.example.mscheckout;

import com.example.mscheckout.dto.CheckoutRequestDTO;
import com.example.mscheckout.dto.CheckoutResultDTO;
import com.example.mscheckout.dto.ItemCheckoutDTO;
import com.example.mscheckout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CheckoutService service;

    @BeforeEach
    void setUp() {
        // Inyectar URLs y el RestTemplate mockeado vía ReflectionTestUtils
        ReflectionTestUtils.setField(service, "productosUrl",  "http://ms-productos");
        ReflectionTestUtils.setField(service, "inventarioUrl", "http://ms-inventario");
        ReflectionTestUtils.setField(service, "pedidosUrl",    "http://ms-pedidos");
        ReflectionTestUtils.setField(service, "restTemplate",  restTemplate);
    }

    // ── procesarCompra exitosa ────────────────────────────────────────────────

    @Test
    void deberiaRetornarResultadoConfirmadoCuandoCompraEsExitosa() {

        // Item del carrito
        ItemCheckoutDTO item = new ItemCheckoutDTO();
        item.setProductoId(1L);
        item.setCantidad(2);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(List.of(item));

        // Mock respuesta de ms-productos
        Map<String, Object> producto = Map.of(
                "id", 1,
                "nombre", "Laptop Dell",
                "precio", 999.99
        );
        when(restTemplate.exchange(
                contains("/api/v1/productos/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(ResponseEntity.ok(producto));

        // Mock respuesta de ms-inventario (descontar stock)
        when(restTemplate.exchange(
                contains("/descontar"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("mensaje", "Stock descontado")));

        // Mock respuesta de ms-pedidos
        Map<String, Object> pedido = Map.of("id", 100);
        when(restTemplate.exchange(
                contains("/api/v1/pedidos"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(pedido));

        CheckoutResultDTO resultado = service.procesarCompra("juan", request, "token-jwt");

        assertNotNull(resultado);
        assertEquals("juan", resultado.getUsername());
        assertEquals("CONFIRMADO", resultado.getEstado());
        assertEquals(100L, resultado.getPedidoId());
        assertEquals(1999.98, resultado.getTotal(), 0.01);
        assertEquals("Compra realizada exitosamente", resultado.getMensaje());
    }

    // ── carrito vacío ─────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoCarritoEstaVacio() {

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.procesarCompra("juan", request, "token-jwt"));

        assertTrue(ex.getMessage().contains("vacío"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoItemsEsNull() {

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.procesarCompra("juan", request, "token-jwt"));

        assertTrue(ex.getMessage().contains("vacío"));
    }

    // ── cantidad inválida ─────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoCantidadEsCero() {

        ItemCheckoutDTO item = new ItemCheckoutDTO();
        item.setProductoId(1L);
        item.setCantidad(0);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(List.of(item));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.procesarCompra("juan", request, "token-jwt"));

        assertTrue(ex.getMessage().contains("Cantidad inválida"));
    }

    // ── producto no encontrado ────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {

        ItemCheckoutDTO item = new ItemCheckoutDTO();
        item.setProductoId(99L);
        item.setCantidad(1);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(List.of(item));

        when(restTemplate.exchange(
                contains("/api/v1/productos/99"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.procesarCompra("juan", request, "token-jwt"));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    // ── stock insuficiente ────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoStockEsInsuficiente() {

        ItemCheckoutDTO item = new ItemCheckoutDTO();
        item.setProductoId(1L);
        item.setCantidad(100);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setItems(List.of(item));

        Map<String, Object> producto = Map.of(
                "id", 1,
                "nombre", "Laptop Dell",
                "precio", 999.99
        );
        when(restTemplate.exchange(
                contains("/api/v1/productos/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(ResponseEntity.ok(producto));

        when(restTemplate.exchange(
                contains("/descontar"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.procesarCompra("juan", request, "token-jwt"));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }
}
