package com.example.mscomputer.repository;

import com.example.mscomputer.model.Computer;
import com.example.mscomputer.model.Computer.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComputerRepository extends JpaRepository<Computer, Long> {
    List<Computer> findByEstado(Estado estado);
    List<Computer> findByMarcaIgnoreCase(String marca);
}
