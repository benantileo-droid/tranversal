package com.example.mscomputer;

import com.example.mscomputer.controller.ComputerController;
import com.example.mscomputer.model.Computer;
import com.example.mscomputer.security.jwt.JwtService;
import com.example.mscomputer.service.ComputerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComputerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComputerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComputerService service;

    @MockitoBean
    private JwtService jwtService;

    // ── GET /api/computers/{id} ───────────────────────────────────────────────

    @Test
    void deberiaRetornarComputerPorId() throws Exception {

        Computer computer = new Computer();
        computer.setId(1L);
        computer.setNombre("MacBook Pro");
        computer.setMarca("Apple");
        computer.setModelo("M3");
        computer.setProcesador("Apple M3");
        computer.setRam("16GB");
        computer.setAlmacenamiento("512GB SSD");
        computer.setPrecio(new BigDecimal("1999.99"));
        computer.setEstado(Computer.Estado.DISPONIBLE);

        when(service.findById(1L))
                .thenReturn(computer);

        mockMvc.perform(get("/api/computers/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("MacBook Pro"))
                .andExpect(jsonPath("$.marca").value("Apple"))
                .andExpect(jsonPath("$.ram").value("16GB"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));

        verify(service).findById(1L);
    }

    // ── POST /api/computers ───────────────────────────────────────────────────

    @Test
    void deberiaCrearComputer() throws Exception {

        Computer computer = new Computer();
        computer.setId(1L);
        computer.setNombre("MacBook Pro");
        computer.setMarca("Apple");
        computer.setModelo("M3");
        computer.setProcesador("Apple M3");
        computer.setRam("16GB");
        computer.setAlmacenamiento("512GB SSD");
        computer.setPrecio(new BigDecimal("1999.99"));
        computer.setEstado(Computer.Estado.DISPONIBLE);

        when(service.save(any(Computer.class)))
                .thenReturn(computer);

        String json = """
                {
                    "nombre": "MacBook Pro",
                    "marca": "Apple",
                    "modelo": "M3",
                    "procesador": "Apple M3",
                    "ram": "16GB",
                    "almacenamiento": "512GB SSD",
                    "precio": 1999.99
                }
                """;

        mockMvc.perform(post("/api/computers")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("MacBook Pro"))
                .andExpect(jsonPath("$.marca").value("Apple"))
                .andExpect(jsonPath("$.modelo").value("M3"))
                .andExpect(jsonPath("$.precio").value(1999.99))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));

        verify(service).save(any(Computer.class));
    }

    @Test
    void deberiaRetornar400CuandoFaltanCamposObligatorios() throws Exception {

        String json = """
                {
                    "descripcion": "Sin campos obligatorios"
                }
                """;

        mockMvc.perform(post("/api/computers")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/computers/{id} ───────────────────────────────────────────────

    @Test
    void deberiaActualizarComputer() throws Exception {

        Computer computer = new Computer();
        computer.setId(1L);
        computer.setNombre("MacBook Pro Max");
        computer.setMarca("Apple");
        computer.setModelo("M3 Max");
        computer.setProcesador("Apple M3 Max");
        computer.setRam("32GB");
        computer.setAlmacenamiento("1TB SSD");
        computer.setPrecio(new BigDecimal("2999.99"));
        computer.setEstado(Computer.Estado.DISPONIBLE);

        when(service.update(eq(1L), any(Computer.class)))
                .thenReturn(computer);

        String json = """
                {
                    "nombre": "MacBook Pro Max",
                    "marca": "Apple",
                    "modelo": "M3 Max",
                    "procesador": "Apple M3 Max",
                    "ram": "32GB",
                    "almacenamiento": "1TB SSD",
                    "precio": 2999.99
                }
                """;

        mockMvc.perform(put("/api/computers/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("MacBook Pro Max"))
                .andExpect(jsonPath("$.ram").value("32GB"))
                .andExpect(jsonPath("$.precio").value(2999.99));

        verify(service).update(eq(1L), any(Computer.class));
    }

    // ── GET /api/computers/estado/{estado} ────────────────────────────────────

    @Test
    void deberiaRetornarComputersPorEstado() throws Exception {

        Computer c1 = new Computer();
        c1.setId(1L);
        c1.setNombre("MacBook Pro");
        c1.setMarca("Apple");
        c1.setModelo("M3");
        c1.setProcesador("Apple M3");
        c1.setRam("16GB");
        c1.setAlmacenamiento("512GB SSD");
        c1.setPrecio(new BigDecimal("1999.99"));
        c1.setEstado(Computer.Estado.DISPONIBLE);

        Computer c2 = new Computer();
        c2.setId(2L);
        c2.setNombre("Dell XPS 15");
        c2.setMarca("Dell");
        c2.setModelo("XPS 15");
        c2.setProcesador("Intel i9");
        c2.setRam("32GB");
        c2.setAlmacenamiento("1TB SSD");
        c2.setPrecio(new BigDecimal("1799.99"));
        c2.setEstado(Computer.Estado.DISPONIBLE);

        when(service.findByEstado(Computer.Estado.DISPONIBLE))
                .thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/computers/estado/DISPONIBLE")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"))
                .andExpect(jsonPath("$[1].estado").value("DISPONIBLE"));

        verify(service).findByEstado(Computer.Estado.DISPONIBLE);
    }

    // ── GET /api/computers/marca/{marca} ──────────────────────────────────────

    @Test
    void deberiaRetornarComputersPorMarca() throws Exception {

        Computer c1 = new Computer();
        c1.setId(1L);
        c1.setNombre("MacBook Pro");
        c1.setMarca("Apple");
        c1.setModelo("M3");
        c1.setProcesador("Apple M3");
        c1.setRam("16GB");
        c1.setAlmacenamiento("512GB SSD");
        c1.setPrecio(new BigDecimal("1999.99"));

        Computer c2 = new Computer();
        c2.setId(2L);
        c2.setNombre("MacBook Air");
        c2.setMarca("Apple");
        c2.setModelo("M2");
        c2.setProcesador("Apple M2");
        c2.setRam("8GB");
        c2.setAlmacenamiento("256GB SSD");
        c2.setPrecio(new BigDecimal("1099.99"));

        when(service.findByMarca("Apple"))
                .thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/computers/marca/Apple")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].marca").value("Apple"))
                .andExpect(jsonPath("$[1].marca").value("Apple"));

        verify(service).findByMarca("Apple");
    }

    // ── DELETE /api/computers/{id} ────────────────────────────────────────────

    @Test
    void deberiaEliminarComputer() throws Exception {

        mockMvc.perform(delete("/api/computers/1")
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
