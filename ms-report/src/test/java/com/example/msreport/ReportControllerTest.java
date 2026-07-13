package com.example.msreport;

import com.example.msreport.controller.ReportController;
import com.example.msreport.model.Report;
import com.example.msreport.security.jwt.JwtService;
import com.example.msreport.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService service;

    @MockitoBean
    private JwtService jwtService;

    // ── GET /api/reports/{id} ─────────────────────────────────────────────────

    @Test
    void deberiaRetornarReportPorId() throws Exception {

        Report report = new Report();
        report.setId(1L);
        report.setTipoReporte("MENSUAL");
        report.setDescripcion("Reporte mensual de actividad");
        report.setGeneradoPorUserId(5L);

        when(service.findById(1L))
                .thenReturn(report);

        mockMvc.perform(get("/api/reports/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoReporte").value("MENSUAL"))
                .andExpect(jsonPath("$.descripcion").value("Reporte mensual de actividad"))
                .andExpect(jsonPath("$.generadoPorUserId").value(5));

        verify(service).findById(1L);
    }

    // ── POST /api/reports ─────────────────────────────────────────────────────

    @Test
    void deberiaCrearReport() throws Exception {

        Report report = new Report();
        report.setId(1L);
        report.setTipoReporte("MENSUAL");
        report.setDescripcion("Reporte mensual de actividad");
        report.setGeneradoPorUserId(5L);

        when(service.save(any(Report.class)))
                .thenReturn(report);

        String json = """
                {
                    "tipoReporte": "MENSUAL",
                    "descripcion": "Reporte mensual de actividad",
                    "generadoPorUserId": 5
                }
                """;

        mockMvc.perform(post("/api/reports")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoReporte").value("MENSUAL"))
                .andExpect(jsonPath("$.generadoPorUserId").value(5));

        verify(service).save(any(Report.class));
    }

    @Test
    void deberiaRetornar400CuandoFaltanCamposObligatorios() throws Exception {

        String json = """
                {
                    "descripcion": "Sin tipo ni usuario"
                }
                """;

        mockMvc.perform(post("/api/reports")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/reports/tipo/{tipo} ──────────────────────────────────────────

    @Test
    void deberiaRetornarReportsPorTipo() throws Exception {

        Report r1 = new Report();
        r1.setId(1L);
        r1.setTipoReporte("MENSUAL");
        r1.setGeneradoPorUserId(5L);

        Report r2 = new Report();
        r2.setId(2L);
        r2.setTipoReporte("MENSUAL");
        r2.setGeneradoPorUserId(7L);

        when(service.findByTipo("MENSUAL"))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/reports/tipo/MENSUAL")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoReporte").value("MENSUAL"))
                .andExpect(jsonPath("$[1].tipoReporte").value("MENSUAL"));

        verify(service).findByTipo("MENSUAL");
    }

    // ── GET /api/reports/user/{userId} ────────────────────────────────────────

    @Test
    void deberiaRetornarReportsPorUserId() throws Exception {

        Report r1 = new Report();
        r1.setId(1L);
        r1.setTipoReporte("MENSUAL");
        r1.setGeneradoPorUserId(5L);

        Report r2 = new Report();
        r2.setId(2L);
        r2.setTipoReporte("ANUAL");
        r2.setGeneradoPorUserId(5L);

        when(service.findByUser(5L))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/reports/user/5")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].generadoPorUserId").value(5))
                .andExpect(jsonPath("$[1].generadoPorUserId").value(5));

        verify(service).findByUser(5L);
    }

    // ── DELETE /api/reports/{id} ──────────────────────────────────────────────

    @Test
    void deberiaEliminarReport() throws Exception {

        mockMvc.perform(delete("/api/reports/1")
                        .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
