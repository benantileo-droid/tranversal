package com.example.mspedidos;

import com.example.mspedidos.dto.ItemPedidoDTO;
import com.example.mspedidos.dto.PedidoRequestDTO;
import com.example.mspedidos.model.DetallePedido;
import com.example.mspedidos.model.Pedido;
import com.example.mspedidos.repository.PedidoRepository;
import com.example.mspedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService service;

    @BeforeEach
    void setUp() {
        // Inyectamos el mock de RestTemplate y la URL del servicio de productos
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "productosUrl", "http://localhost:8082");
    }

    @Test
    void deberiaCrearPedidoCorrectamente() {

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setPrecioUnit(999.99);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of(item));

        Map<String, Object> productoResp = Map.of(
                "id", 1,
                "nombre", "Laptop",
                "precio", 999.99,
                "stock", 10
        );

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(productoResp));

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setUsername("juan");
        pedidoGuardado.setFecha(LocalDateTime.now());
        pedidoGuardado.setTotal(1999.98);
        pedidoGuardado.setEstado("CONFIRMADO");

        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoGuardado);

        Pedido resultado = service.crearPedido("juan", request, "token.jwt.mock");

        assertNotNull(resultado);
        assertEquals("juan", resultado.getUsername());
        assertEquals("CONFIRMADO", resultado.getEstado());
        assertEquals(1999.98, resultado.getTotal());

        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoItemsEstaVacio() {

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crearPedido("juan", request, "token.jwt.mock"));

        assertEquals("El pedido debe tener al menos un item", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoItemsEsNull() {

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crearPedido("juan", request, "token.jwt.mock"));

        assertEquals("El pedido debe tener al menos un item", ex.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoEncontrado() {

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(99L);
        item.setCantidad(1);
        item.setPrecioUnit(50.0);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setItems(List.of(item));

        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Not Found",
                        null, null, null));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crearPedido("juan", request, "token.jwt.mock"));

        assertTrue(ex.getMessage().contains("Producto no encontrado: id=99"));
    }

    @Test
    void deberiaRetornarPedidosPorUsername() {

        Pedido p1 = new Pedido(1L, "juan", LocalDateTime.now(), 500.0, "CONFIRMADO", List.of());
        Pedido p2 = new Pedido(2L, "juan", LocalDateTime.now(), 200.0, "CONFIRMADO", List.of());

        Mockito.when(pedidoRepository.findByUsername("juan"))
                .thenReturn(List.of(p1, p2));

        List<Pedido> resultado = service.listarPorUsuario("juan");

        assertEquals(2, resultado.size());
        assertEquals("juan", resultado.get(0).getUsername());

        verify(pedidoRepository).findByUsername("juan");
    }

    @Test
    void deberiaRetornarTodosLosPedidos() {

        Pedido p1 = new Pedido(1L, "juan", LocalDateTime.now(), 500.0, "CONFIRMADO", List.of());
        Pedido p2 = new Pedido(2L, "maria", LocalDateTime.now(), 300.0, "CONFIRMADO", List.of());

        Mockito.when(pedidoRepository.findAll())
                .thenReturn(List.of(p1, p2));

        List<Pedido> resultado = service.listarTodos();

        assertEquals(2, resultado.size());

        verify(pedidoRepository).findAll();
    }
}
