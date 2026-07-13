package com.example.productos.controller;

import com.example.productos.model.Producto;
import com.example.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Productos", description = "Operaciones sobre productos de la tienda")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @Operation(summary = "Listar todos los productos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Producto> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Descontar stock de un producto", description = "Solo ADMIN puede descontar stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock actualizado"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente o producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PutMapping("/{id}/descontar-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> descontarStock(@PathVariable Long id, @RequestParam int cantidad) {
        boolean ok = service.descontarStock(id, cantidad);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error", "Stock insuficiente o producto no encontrado"));
        return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado"));
    }
}
