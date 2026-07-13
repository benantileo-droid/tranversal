package com.example.msdelivery;

import com.example.msdelivery.controller.DeliveryController;
import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.security.jwt.JwtService;
import com.example.msdelivery.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService service;

    @MockitoBean
    private JwtService jwtService;

    // ── GET /api/deliveries/{id} ──────────────────────────────────────────────

    @Test
    void deberiaRetornarDeliveryPorId() throws Exception {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setFechaEnvio(LocalDate.of(2026, 6, 20));
        delivery.setEstado(Delivery.Estado.PENDIENTE);

        when(service.findById(1L))
                .thenReturn(delivery);

        mockMvc.perform(get("/api/deliveries/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationId").value(10))
                .andExpect(jsonPath("$.direccionEntrega").value("Av. Siempre Viva 123, Santiago"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(service).findById(1L);
    }

    // ── POST /api/deliveries ──────────────────────────────────────────────────

    @Test
    void deberiaCrearDelivery() throws Exception {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setFechaEnvio(LocalDate.of(2026, 6, 20));
        delivery.setEstado(Delivery.Estado.PENDIENTE);

        when(service.save(any(Delivery.class)))
                .thenReturn(delivery);

        String json = """
                {
                    "reservationId": 10,
                    "direccionEntrega": "Av. Siempre Viva 123, Santiago",
                    "fechaEnvio": "2026-06-20"
                }
                """;

        mockMvc.perform(post("/api/deliveries")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationId").value(10))
                .andExpect(jsonPath("$.direccionEntrega").value("Av. Siempre Viva 123, Santiago"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(service).save(any(Delivery.class));
    }

    @Test
    void deberiaRetornar400CuandoFaltanCamposObligatorios() throws Exception {

        String json = """
                {
                    "fechaEnvio": "2026-06-20"
                }
                """;

        mockMvc.perform(post("/api/deliveries")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/deliveries/{id}/estado ─────────────────────────────────────

    @Test
    void deberiaActualizarEstadoDeDelivery() throws Exception {

        Delivery delivery = new Delivery();
        delivery.setId(1L);
        delivery.setReservationId(10L);
        delivery.setDireccionEntrega("Av. Siempre Viva 123, Santiago");
        delivery.setEstado(Delivery.Estado.EN_CAMINO);

        when(service.updateEstado(eq(1L), eq(Delivery.Estado.EN_CAMINO)))
                .thenReturn(delivery);

        mockMvc.perform(patch("/api/deliveries/1/estado")
                        .param("estado", "EN_CAMINO")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));

        verify(service).updateEstado(1L, Delivery.Estado.EN_CAMINO);
    }

    // ── GET /api/deliveries/estado/{estado} ───────────────────────────────────

    @Test
    void deberiaRetornarDeliveriesPorEstado() throws Exception {

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

        when(service.findByEstado(Delivery.Estado.EN_CAMINO))
                .thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/deliveries/estado/EN_CAMINO")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("EN_CAMINO"))
                .andExpect(jsonPath("$[1].estado").value("EN_CAMINO"));

        verify(service).findByEstado(Delivery.Estado.EN_CAMINO);
    }

    // ── DELETE /api/deliveries/{id} ───────────────────────────────────────────

    @Test
    void deberiaEliminarDelivery() throws Exception {

        mockMvc.perform(delete("/api/deliveries/1")
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
