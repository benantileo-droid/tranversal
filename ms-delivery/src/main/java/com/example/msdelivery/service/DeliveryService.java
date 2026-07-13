package com.example.msdelivery.service;

import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repo;

    public List<Delivery> findAll() { return repo.findAll(); }

    public Delivery findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery no encontrado: " + id));
    }

    public Delivery save(Delivery d) { return repo.save(d); }

    public Delivery updateEstado(Long id, Delivery.Estado estado) {
        Delivery d = findById(id);
        d.setEstado(estado);
        return repo.save(d);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public List<Delivery> findByEstado(Delivery.Estado estado) {
        return repo.findByEstado(estado);
    }
}
