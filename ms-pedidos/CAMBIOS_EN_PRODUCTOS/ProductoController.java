package com.example.productos.controller;

import com.example.productos.model.Producto;
import com.example.productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // CAMBIO: USER también puede consultar por id (necesario para ms-pedidos)
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // NUEVO: endpoint para descontar stock (llamado por ms-pedidos con token ADMIN o USER)
    @PutMapping("/{id}/descontar-stock")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> descontarStock(@PathVariable Long id, @RequestParam int cantidad) {
        boolean ok = service.descontarStock(id, cantidad);
        if (!ok) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Stock insuficiente o producto no encontrado"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado correctamente"));
    }
}
