package com.example.msreview.repository;

import com.example.msreview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByComputerId(Long computerId);
    List<Review> findByUserId(Long userId);
}
