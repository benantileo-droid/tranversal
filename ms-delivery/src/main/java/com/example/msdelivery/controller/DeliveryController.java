package com.example.msdelivery.controller;

import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deliveries", description = "Gestión de entregas de pedidos")
public class DeliveryController {

    private final DeliveryService service;

    @Operation(summary = "Listar todas las entregas")
    @GetMapping
    public List<Delivery> getAll() { return service.findAll(); }

    @Operation(summary = "Obtener entrega por ID")
    @GetMapping("/{id}")
    public Delivery getById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Crear nueva entrega")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entrega creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Delivery> create(@Valid @RequestBody Delivery d) {
        return ResponseEntity.ok(service.save(d));
    }

    @Operation(summary = "Actualizar estado de entrega")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Delivery> updateEstado(@PathVariable Long id, @RequestParam Delivery.Estado estado) {
        return ResponseEntity.ok(service.updateEstado(id, estado));
    }

    @Operation(summary = "Eliminar entrega")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar entregas por estado")
    @GetMapping("/estado/{estado}")
    public List<Delivery> byEstado(@PathVariable Delivery.Estado estado) {
        return service.findByEstado(estado);
    }
}
