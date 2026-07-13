package com.example.msinventario.service;

import com.example.msinventario.model.Stock;
import com.example.msinventario.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository repository;

    public List<Stock> listarTodos() {
        return repository.findAll();
    }

    public Optional<Stock> obtenerPorProducto(Long productoId) {
        return repository.findByProductoId(productoId);
    }


    @Transactional
    public boolean descontar(Long productoId, int cantidad) {
        return repository.findByProductoId(productoId).map(stock -> {
            if (stock.getCantidad() < cantidad) return false;
            stock.setCantidad(stock.getCantidad() - cantidad);
            repository.save(stock);
            return true;
        }).orElse(false);
    }


    @Transactional
    public boolean reponer(Long productoId, int cantidad) {
        return repository.findByProductoId(productoId).map(stock -> {
            stock.setCantidad(stock.getCantidad() + cantidad);
            repository.save(stock);
            return true;
        }).orElse(false);
    }


    public Stock registrar(Long productoId, String nombre, int cantidad, int stockMin) {
        Stock stock = new Stock(null, productoId, nombre, cantidad, stockMin);
        return repository.save(stock);
    }


    public List<Stock> productosBajoMinimo() {
        return repository.findAll().stream()
                .filter(s -> s.getCantidad() <= s.getStockMin())
                .toList();
    }
}
