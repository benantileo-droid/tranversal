package com.example.ms_user.controller;

import com.example.ms_user.model.User;
import com.example.ms_user.security.jwt.JwtService;
import com.example.ms_user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, login y validación de tokens JWT")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario con rol USER por defecto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String role = body.getOrDefault("role", "USER");

            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username y password son requeridos"));
            }

            if (!role.equals("USER") && !role.equals("ADMIN")) {
                role = "USER";
            }

            userService.register(username, password, role);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuario registrado correctamente",
                    "role", role
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El usuario ya existe"));
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            if (auth.isAuthenticated()) {
                User user = userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                String token = jwtService.generateToken(username, user.getRole());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "username", username,
                        "role", user.getRole()
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la solicitud"));
        }
    }

    @Operation(summary = "Validar token JWT", description = "Verifica si el token del usuario es válido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido, expirado o ausente")
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token ausente"));
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token inválido o expirado"));
        }

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", jwtService.extractUsername(token),
                "role", jwtService.extractRole(token)
        ));
    }
}
