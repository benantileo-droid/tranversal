package com.example.mspedidos.controller;

import com.example.mspedidos.dto.PedidoRequestDTO;
import com.example.mspedidos.model.Pedido;
import com.example.mspedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos", description = "Gestión de pedidos de usuarios")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido para el usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al procesar el pedido"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO request,
                                         HttpServletRequest httpRequest) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
            Pedido pedido = pedidoService.crearPedido(username, request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Mis pedidos", description = "Lista los pedidos del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Pedido>> misPedidos() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(pedidoService.listarPorUsuario(username));
    }

    @Operation(summary = "Listar todos los pedidos", description = "Solo ADMIN puede ver todos los pedidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista completa de pedidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Pedido>> todos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }
}
