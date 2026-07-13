package com.example.msreview.controller;

import com.example.msreview.model.Review;
import com.example.msreview.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reviews", description = "Reseñas de computadores por usuarios")
public class ReviewController {

    private final ReviewService service;

    @Operation(summary = "Listar todas las reseñas")
    @GetMapping
    public List<Review> getAll() { return service.findAll(); }

    @Operation(summary = "Obtener reseña por ID")
    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) { return service.findById(id); }

    @Operation(summary = "Crear reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review r) {
        return ResponseEntity.ok(service.save(r));
    }

    @Operation(summary = "Eliminar reseña")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reseñas por computador")
    @GetMapping("/computer/{computerId}")
    public List<Review> byComputer(@PathVariable Long computerId) {
        return service.findByComputer(computerId);
    }

    @Operation(summary = "Reseñas por usuario")
    @GetMapping("/user/{userId}")
    public List<Review> byUser(@PathVariable Long userId) {
        return service.findByUser(userId);
    }
}
