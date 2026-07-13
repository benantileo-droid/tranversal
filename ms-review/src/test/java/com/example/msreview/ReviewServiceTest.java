package com.example.msreview;

import com.example.msreview.model.Review;
import com.example.msreview.repository.ReviewRepository;
import com.example.msreview.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository repo;

    @InjectMocks
    private ReviewService service;

    private Review review;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .id(1L)
                .userId(2L)
                .computerId(5L)
                .puntuacion(4)
                .comentario("Excelente computador")
                .fecha(LocalDate.now())
                .build();
    }

    @Test
    void findAll_debeRetornarListaDeReviews() {
        when(repo.findAll()).thenReturn(List.of(review));

        List<Review> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Excelente computador", result.get(0).getComentario());
        verify(repo, times(1)).findAll();
    }

    @Test
    void findById_debeRetornarReview_cuandoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.of(review));

        Review result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(4, result.getPuntuacion());
    }

    @Test
    void findById_debeLanzarExcepcion_cuandoNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findById(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void save_debeGuardarYRetornarReview() {
        when(repo.save(review)).thenReturn(review);

        Review result = service.save(review);

        assertNotNull(result);
        assertEquals(5L, result.getComputerId());
        verify(repo, times(1)).save(review);
    }

    @Test
    void delete_debeEliminarReviewPorId() {
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void findByComputer_debeRetornarReviewsDelComputador() {
        when(repo.findByComputerId(5L)).thenReturn(List.of(review));

        List<Review> result = service.findByComputer(5L);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getComputerId());
    }

    @Test
    void findByUser_debeRetornarReviewsDelUsuario() {
        when(repo.findByUserId(2L)).thenReturn(List.of(review));

        List<Review> result = service.findByUser(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());
    }
}
