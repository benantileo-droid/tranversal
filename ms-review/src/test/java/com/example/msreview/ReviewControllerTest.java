package com.example.msreview;

import com.example.msreview.controller.ReviewController;
import com.example.msreview.model.Review;
import com.example.msreview.security.jwt.JwtService;
import com.example.msreview.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService service;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAll_debeRetornarListaDeReviews() throws Exception {
        when(service.findAll()).thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comentario").value("Excelente computador"))
                .andExpect(jsonPath("$[0].puntuacion").value(4));
    }

    @Test
    void getById_debeRetornarReview() throws Exception {
        when(service.findById(1L)).thenReturn(review);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void create_debeGuardarYRetornarReview() throws Exception {
        when(service.save(any(Review.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.computerId").value(5));
    }

    @Test
    void delete_debeRetornarNoContent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    @Test
    void byComputer_debeRetornarReviewsDelComputador() throws Exception {
        when(service.findByComputer(5L)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews/computer/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].computerId").value(5));
    }

    @Test
    void byUser_debeRetornarReviewsDelUsuario() throws Exception {
        when(service.findByUser(2L)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/reviews/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }
}
