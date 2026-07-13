package com.example.msreport.controller;

import com.example.msreport.model.Report;
import com.example.msreport.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Generación y consulta de reportes del sistema")
public class ReportController {

    private final ReportService service;

    @Operation(summary = "Listar todos los reportes")
    @GetMapping
    public List<Report> getAll() { return service.findAll(); }

    @Operation(summary = "Obtener reporte por ID")
    @GetMapping("/{id}")
    public Report getById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Crear nuevo reporte")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Report> create(@Valid @RequestBody Report r) {
        return ResponseEntity.ok(service.save(r));
    }

    @Operation(summary = "Eliminar reporte")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar reportes por tipo")
    @GetMapping("/tipo/{tipo}")
    public List<Report> byTipo(@PathVariable String tipo) {
        return service.findByTipo(tipo);
    }

    @Operation(summary = "Filtrar reportes por usuario generador")
    @GetMapping("/user/{userId}")
    public List<Report> byUser(@PathVariable Long userId) {
        return service.findByUser(userId);
    }
}
