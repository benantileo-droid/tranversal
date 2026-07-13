package com.example.msinventario.controller;

import com.example.msinventario.model.Stock;
import com.example.msinventario.service.StockService;
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
@RequestMapping("/api/v1/inventario")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inventario", description = "Gestión del stock de productos")
public class StockController {

    @Autowired
    private StockService service;

    @Operation(summary = "Listar todo el stock", description = "Solo ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de stock"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Stock> listar() { return service.listarTodos(); }

    @Operation(summary = "Obtener stock por ID de producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{productoId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> obtener(@PathVariable Long productoId) {
        return service.obtenerPorProducto(productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Descontar stock de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock descontado"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente")
    })
    @PutMapping("/{productoId}/descontar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> descontar(@PathVariable Long productoId, @RequestParam int cantidad) {
        boolean ok = service.descontar(productoId, cantidad);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error", "Stock insuficiente o producto no encontrado"));
        return ResponseEntity.ok(Map.of("mensaje", "Stock descontado correctamente"));
    }

    @Operation(summary = "Reponer stock de un producto", description = "Solo ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock repuesto"),
        @ApiResponse(responseCode = "400", description = "Producto no encontrado")
    })
    @PutMapping("/{productoId}/reponer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reponer(@PathVariable Long productoId, @RequestParam int cantidad) {
        boolean ok = service.reponer(productoId, cantidad);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error", "Producto no encontrado en inventario"));
        return ResponseEntity.ok(Map.of("mensaje", "Stock repuesto correctamente"));
    }

    @Operation(summary = "Registrar nuevo producto en inventario", description = "Solo ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock registrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> registrar(@RequestBody Map<String, Object> body) {
        Long productoId = Long.valueOf(body.get("productoId").toString());
        String nombre   = body.get("nombre").toString();
        int cantidad    = Integer.parseInt(body.get("cantidad").toString());
        int stockMin    = body.containsKey("stockMin") ? Integer.parseInt(body.get("stockMin").toString()) : 5;
        return ResponseEntity.ok(service.registrar(productoId, nombre, cantidad, stockMin));
    }

    @Operation(summary = "Ver alertas de stock bajo mínimo", description = "Solo ADMIN")
    @GetMapping("/alertas")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Stock> alertas() { return service.productosBajoMinimo(); }
}
