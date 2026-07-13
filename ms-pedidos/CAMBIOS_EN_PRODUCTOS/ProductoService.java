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

    // NUEVO: descuenta stock. Retorna false si no hay suficiente stock.
    @Transactional
    public boolean descontarStock(Long productoId, int cantidad) {
        return repository.findById(productoId).map(producto -> {
            if (producto.getStock() < cantidad) {
                return false;
            }
            producto.setStock(producto.getStock() - cantidad);
            repository.save(producto);
            return true;
        }).orElse(false);
    }
}
