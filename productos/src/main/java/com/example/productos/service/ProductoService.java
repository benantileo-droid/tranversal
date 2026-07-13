package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;


    public List<Producto> listar() {
        return repository.findAll();
    }

    public Optional<Producto> obtener(Long id) {
        return repository.findById(id);
    }
    @Transactional
    public boolean descontarStock(Long productoId, int cantidad) {
        return repository.findById(productoId).map(p -> {
            if (p.getStock() < cantidad) return false;
            p.setStock(p.getStock() - cantidad);
            repository.save(p);
            return true;
        }).orElse(false);
    }

}
