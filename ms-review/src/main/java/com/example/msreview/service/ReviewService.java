package com.example.msreview.service;

import com.example.msreview.model.Review;
import com.example.msreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repo;

    public List<Review> findAll() { return repo.findAll(); }

    public Review findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review no encontrada: " + id));
    }

    public Review save(Review r) { return repo.save(r); }

    public void delete(Long id) { repo.deleteById(id); }

    public List<Review> findByComputer(Long computerId) {
        return repo.findByComputerId(computerId);
    }

    public List<Review> findByUser(Long userId) {
        return repo.findByUserId(userId);
    }
}
