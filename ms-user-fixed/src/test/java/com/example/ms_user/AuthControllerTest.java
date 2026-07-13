package com.example.ms_user;

import com.example.ms_user.controller.AuthController;
import com.example.ms_user.model.User;
import com.example.ms_user.security.jwt.JwtService;
import com.example.ms_user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authManager;

    @Test
    void deberiaRegistrarUsuarioCorrectamente() throws Exception {

        String json = """
                {
                    "username": "juan",
                    "password": "password123",
                    "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void deberiaRetornar400CuandoFaltanCamposEnRegistro() throws Exception {

        String json = """
                {
                    "username": ""
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username y password son requeridos"));
    }

    @Test
    void deberiaRetornar409CuandoUsuarioYaExiste() throws Exception {

        when(userService.register(any(), any(), any()))
                .thenThrow(new RuntimeException("Usuario ya existe"));

        String json = """
                {
                    "username": "juan",
                    "password": "password123",
                    "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El usuario ya existe"));
    }

    @Test
    void deberiaLoginExitosoYRetornarToken() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("juan");
        user.setPassword("encoded_pass");
        user.setRole("USER");

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("juan", "password123");
        authToken.setDetails(user);

        when(authManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("juan", null, java.util.List.of()));

        when(userService.findByUsername("juan"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken("juan", "USER"))
                .thenReturn("mocked.jwt.token");

        String json = """
                {
                    "username": "juan",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void deberiaRetornar401CuandoCredencialesInvalidas() throws Exception {

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        String json = """
                {
                    "username": "juan",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuario o contraseña incorrectos"));
    }

    @Test
    void deberiaRetornar401CuandoNoHayTokenEnValidate() throws Exception {
        // Given: no se envía header Authorization

        // When / Then
        mockMvc.perform(get("/api/v1/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void deberiaRetornar401CuandoTokenEsInvalidoEnValidate() throws Exception {
        // Given
        when(jwtService.isTokenValid("token.invalido")).thenReturn(false);

        // When / Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer token.invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void deberiaValidarTokenCorrectamente() throws Exception {
        // Given
        when(jwtService.isTokenValid("token.valido")).thenReturn(true);
        when(jwtService.extractUsername("token.valido")).thenReturn("juan");
        when(jwtService.extractRole("token.valido")).thenReturn("USER");

        // When / Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer token.valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("juan"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
