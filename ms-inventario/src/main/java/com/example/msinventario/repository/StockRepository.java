package com.example.msinventario.repository;

import com.example.msinventario.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductoId(Long productoId);
    List<Stock> findByCantidadLessThanEqual(Integer limite);
}
