package com.example.msreport;

import com.example.msreport.model.Report;
import com.example.msreport.repository.ReportRepository;
import com.example.msreport.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository repo;

    @InjectMocks
    private ReportService service;

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarReportCuandoExiste() {

        Report report = new Report();
        report.setId(1L);
        report.setTipoReporte("MENSUAL");
        report.setDescripcion("Reporte mensual de actividad");
        report.setGeneradoPorUserId(5L);

        Mockito.when(repo.findById(1L))
                .thenReturn(Optional.of(report));

        Report resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("MENSUAL", resultado.getTipoReporte());
        assertEquals(5L, resultado.getGeneradoPorUserId());

        verify(repo).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoReportNoExiste() {

        Mockito.when(repo.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(99L));

        assertTrue(ex.getMessage().contains("99"));

        verify(repo).findById(99L);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarReport() {

        Report report = new Report();
        report.setId(1L);
        report.setTipoReporte("ANUAL");
        report.setDescripcion("Reporte anual de actividad");
        report.setGeneradoPorUserId(5L);

        Mockito.when(repo.save(report))
                .thenReturn(report);

        Report resultado = service.save(report);

        assertNotNull(resultado);
        assertEquals("ANUAL", resultado.getTipoReporte());

        verify(repo).save(report);
    }

    // ── findByTipo ────────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarReportsPorTipo() {

        Report r1 = new Report();
        r1.setId(1L);
        r1.setTipoReporte("MENSUAL");
        r1.setGeneradoPorUserId(5L);

        Report r2 = new Report();
        r2.setId(2L);
        r2.setTipoReporte("MENSUAL");
        r2.setGeneradoPorUserId(7L);

        Mockito.when(repo.findByTipoReporte("MENSUAL"))
                .thenReturn(List.of(r1, r2));

        List<Report> resultado = service.findByTipo("MENSUAL");

        assertEquals(2, resultado.size());
        assertEquals("MENSUAL", resultado.get(0).getTipoReporte());

        verify(repo).findByTipoReporte("MENSUAL");
    }

    // ── findByUser ────────────────────────────────────────────────────────────

    @Test
    void deberiaRetornarReportsPorUserId() {

        Report r1 = new Report();
        r1.setId(1L);
        r1.setTipoReporte("MENSUAL");
        r1.setGeneradoPorUserId(5L);

        Report r2 = new Report();
        r2.setId(2L);
        r2.setTipoReporte("ANUAL");
        r2.setGeneradoPorUserId(5L);

        Mockito.when(repo.findByGeneradoPorUserId(5L))
                .thenReturn(List.of(r1, r2));

        List<Report> resultado = service.findByUser(5L);

        assertEquals(2, resultado.size());

        verify(repo).findByGeneradoPorUserId(5L);
    }
}
