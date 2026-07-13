package com.example.msdelivery;

import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.repository.DeliveryRepository;
import com.example.msdelivery.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository repo;

    @InjectMocks
    private DeliveryService service;

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarDeliveryCuandoExiste() {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setFechaEnvio(LocalDate.of(2026, 6, 20));
        delivery.setEstado(Delivery.Estado.PENDIENTE);

        Mockito.when(repo.findById(1L))
                .thenReturn(Optional.of(delivery));

        Delivery resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Av. Siempre Viva 123, Santiago", resultado.getDireccionEntrega());
        assertEquals(Delivery.Estado.PENDIENTE, resultado.getEstado());

        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoDeliveryNoExiste() {

        Mockito.when(repo.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(99L));

        assertTrue(ex.getMessage().contains("99"));

        verify(repo).findById(99L);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarDelivery() {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setEstado(Delivery.Estado.PENDIENTE);

        Mockito.when(repo.save(delivery))
                .thenReturn(delivery);

        Delivery resultado = service.save(delivery);

        assertNotNull(resultado);
        assertEquals("Av. Siempre Viva 123, Santiago", resultado.getDireccionEntrega());

        verify(repo).save(delivery);
    }

    // ── updateEstado ──────────────────────────────────────────────────────────

    @Test
    void deberiaActualizarEstadoDeDelivery() {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setEstado(Delivery.Estado.PENDIENTE);

        Mockito.when(repo.findById(1L))
                .thenReturn(Optional.of(delivery));

        Mockito.when(repo.save(delivery))
                .thenReturn(delivery);

        Delivery resultado = service.updateEstado(1L, Delivery.Estado.EN_CAMINO);

        assertEquals(Delivery.Estado.EN_CAMINO, resultado.getEstado());

        verify(repo).findById(1L);
        verify(repo).save(delivery);
    }

    // ── findByEstado ──────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarDeliveriesPorEstado() {

        Delivery d1 = new Delivery();
        d1.setId(1L);
        d1.setReservationId(10L);
        d1.setDireccionEntrega("Calle A 1");
        d1.setEstado(Delivery.Estado.EN_CAMINO);

        Delivery d2 = new Delivery();
        d2.setId(2L);
        d2.setReservationId(11L);
        d2.setDireccionEntrega("Calle B 2");
        d2.setEstado(Delivery.Estado.EN_CAMINO);

        Mockito.when(repo.findByEstado(Delivery.Estado.EN_CAMINO))
                .thenReturn(List.of(d1, d2));

        List<Delivery> resultado = service.findByEstado(Delivery.Estado.EN_CAMINO);

        assertEquals(2, resultado.size());
        assertEquals(Delivery.Estado.EN_CAMINO, resultado.get(0).getEstado());

        verify(repo).findByEstado(Delivery.Estado.EN_CAMINO);
    }
}
