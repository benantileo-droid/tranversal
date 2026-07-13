package com.example.ms_user;

import com.example.ms_user.model.User;
import com.example.ms_user.repository.UserRepository;
import com.example.ms_user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void deberiaRetornarUserCuandoExiste() {

        User user = new User();
        user.setId(1L);
        user.setUsername("juan");
        user.setPassword("encoded_pass");
        user.setRole("USER");

        Mockito.when(userRepository.findByUsername("juan"))
                .thenReturn(Optional.of(user));

        Optional<User> resultado = service.findByUsername("juan");

        assertTrue(resultado.isPresent());
        assertEquals("juan", resultado.get().getUsername());
        assertEquals("USER", resultado.get().getRole());

        verify(userRepository).findByUsername("juan");
    }

    @Test
    void deberiaRegistrarUserConRolPorDefecto() {

        User user = User.builder()
                .id(1L)
                .username("maria")
                .password("encoded_pass")
                .role("USER")
                .build();

        Mockito.when(passwordEncoder.encode("password123"))
                .thenReturn("encoded_pass");

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User resultado = service.register("maria", "password123");

        assertEquals("maria", resultado.getUsername());
        assertEquals("USER", resultado.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deberiaRegistrarUserConRolAdmin() {

        User user = User.builder()
                .id(2L)
                .username("admin")
                .password("encoded_pass")
                .role("ADMIN")
                .build();

        Mockito.when(passwordEncoder.encode("adminpass"))
                .thenReturn("encoded_pass");

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User resultado = service.register("admin", "adminpass", "ADMIN");

        assertEquals("admin", resultado.getUsername());
        assertEquals("ADMIN", resultado.getRole());

        verify(userRepository).save(any(User.class));
    }
}
