package com.example.mscomputer.service;

import com.example.mscomputer.model.Computer;
import com.example.mscomputer.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository repo;

    public List<Computer> findAll() { return repo.findAll(); }

    public Computer findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Computer no encontrado: " + id));
    }

    public Computer save(Computer c) { return repo.save(c); }

    public Computer update(Long id, Computer data) {
        Computer c = findById(id);
        c.setNombre(data.getNombre());
        c.setMarca(data.getMarca());
        c.setModelo(data.getModelo());
        c.setProcesador(data.getProcesador());
        c.setRam(data.getRam());
        c.setAlmacenamiento(data.getAlmacenamiento());
        c.setPrecio(data.getPrecio());
        c.setDescripcion(data.getDescripcion());
        c.setEstado(data.getEstado());
        return repo.save(c);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public List<Computer> findByEstado(Computer.Estado estado) {
        return repo.findByEstado(estado);
    }

    public List<Computer> findByMarca(String marca) {
        return repo.findByMarcaIgnoreCase(marca);
    }
}
