package com.example.mspedidos;

import com.example.mspedidos.dto.ItemPedidoDTO;
import com.example.mspedidos.dto.PedidoRequestDTO;
import com.example.mspedidos.model.Pedido;
import com.example.mspedidos.repository.PedidoRepository;
import com.example.mspedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceRestTemplateTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "productosUrl", "http://localhost:8082");
    }

    @Test
    void deberiaLanzarExcepcionCuandoRespuestaProductoEsNull() {

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(1L);
        item.setCantidad(1);
        item.setPrecioUnit(100.0);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of(item));

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(null));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crearPedido("juan", request, "token.jwt.mock"));

        assertTrue(ex.getMessage().contains("Respuesta vacía para productoId: 1"));
    }

    @Test
    void deberiaCalcularTotalCorrectamenteConMultiplesItems() {

        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProductoId(1L);
        item1.setCantidad(2);
        item1.setPrecioUnit(100.0);

        ItemPedidoDTO item2 = new ItemPedidoDTO();
        item2.setProductoId(2L);
        item2.setCantidad(3);
        item2.setPrecioUnit(50.0);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of(item1, item2));

        Map<String, Object> productoResp1 = Map.of("id", 1, "nombre", "Laptop", "precio", 100.0);
        Map<String, Object> productoResp2 = Map.of("id", 2, "nombre", "Mouse", "precio", 50.0);

        when(restTemplate.exchange(
                contains("/1"), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(productoResp1));

        when(restTemplate.exchange(
                contains("/2"), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(productoResp2));

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setUsername("juan");
        pedidoGuardado.setFecha(LocalDateTime.now());
        pedidoGuardado.setTotal(350.0); // 2*100 + 3*50
        pedidoGuardado.setEstado("CONFIRMADO");

        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoGuardado);

        Pedido resultado = service.crearPedido("juan", request, "token.jwt.mock");

        assertNotNull(resultado);
        assertEquals(350.0, resultado.getTotal());
        assertEquals("CONFIRMADO", resultado.getEstado());

        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void deberiaUsarPrecioRealDelProductoIgnorandoPrecioDelItem() {

        // El precioUnit del item es 999 pero el producto real vale 500
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setPrecioUnit(999.0); // precio enviado por el cliente — NO debe usarse

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of(item));

        Map<String, Object> productoResp = Map.of("id", 1, "nombre", "Laptop", "precio", 500.0);

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(productoResp));

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setUsername("juan");
        pedidoGuardado.setFecha(LocalDateTime.now());
        pedidoGuardado.setTotal(1000.0); // 2 * 500 (precio real)
        pedidoGuardado.setEstado("CONFIRMADO");

        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoGuardado);

        Pedido resultado = service.crearPedido("juan", request, "token.jwt.mock");

        // El total debe calcularse con el precio del servicio de productos (500), no del item (999)
        assertEquals(1000.0, resultado.getTotal());

        verify(pedidoRepository).save(any(Pedido.class));
    }
}
