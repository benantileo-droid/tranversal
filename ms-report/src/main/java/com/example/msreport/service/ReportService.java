package com.example.msreport.service;

import com.example.msreport.model.Report;
import com.example.msreport.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository repo;

    public List<Report> findAll() { return repo.findAll(); }

    public Report findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado: " + id));
    }

    public Report save(Report r) { return repo.save(r); }

    public void delete(Long id) { repo.deleteById(id); }

    public List<Report> findByTipo(String tipo) {
        return repo.findByTipoReporte(tipo);
    }

    public List<Report> findByUser(Long userId) {
        return repo.findByGeneradoPorUserId(userId);
    }
}
