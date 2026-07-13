package com.example.ms_user;

import com.example.ms_user.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    // Clave de al menos 32 caracteres para HS256
    private static final String SECRET = "clave-secreta-de-prueba-muy-larga-para-test";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
    }

    @Test
    void deberiaGenerarTokenYExtaerUsername() {

        String token = jwtService.generateToken("juan", "USER");

        assertNotNull(token);
        assertEquals("juan", jwtService.extractUsername(token));
    }

    @Test
    void deberiaExtraerRolDelToken() {

        String token = jwtService.generateToken("admin", "ADMIN");

        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void deberiaValidarTokenValido() {

        String token = jwtService.generateToken("juan", "USER");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void deberiaRetornarFalseConTokenInvalido() {

        String tokenInvalido = "esto.no.es.un.token";

        assertFalse(jwtService.isTokenValid(tokenInvalido));
    }

    @Test
    void deberiaRetornarNullAlExtraerUsernameDeTokenInvalido() {

        String tokenInvalido = "token.invalido.xxx";

        assertNull(jwtService.extractUsername(tokenInvalido));
    }
}
