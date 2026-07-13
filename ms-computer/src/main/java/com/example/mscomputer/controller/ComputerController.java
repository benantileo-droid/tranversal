package com.example.mscomputer.controller;

import com.example.mscomputer.model.Computer;
import com.example.mscomputer.service.ComputerService;
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
@RequestMapping("/api/computers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Computers", description = "CRUD de computadores disponibles en la tienda")
public class ComputerController {

    private final ComputerService service;

    @Operation(summary = "Listar todos los computadores")
    @GetMapping
    public List<Computer> getAll() { return service.findAll(); }

    @Operation(summary = "Obtener computador por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Computador encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    public Computer getById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Crear nuevo computador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Computador creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Computer> create(@Valid @RequestBody Computer c) {
        return ResponseEntity.ok(service.save(c));
    }

    @Operation(summary = "Actualizar computador")
    @PutMapping("/{id}")
    public ResponseEntity<Computer> update(@PathVariable Long id, @Valid @RequestBody Computer c) {
        return ResponseEntity.ok(service.update(id, c));
    }

    @Operation(summary = "Eliminar computador")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar computadores por estado")
    @GetMapping("/estado/{estado}")
    public List<Computer> byEstado(@PathVariable Computer.Estado estado) {
        return service.findByEstado(estado);
    }

    @Operation(summary = "Filtrar computadores por marca")
    @GetMapping("/marca/{marca}")
    public List<Computer> byMarca(@PathVariable String marca) {
        return service.findByMarca(marca);
    }
}
