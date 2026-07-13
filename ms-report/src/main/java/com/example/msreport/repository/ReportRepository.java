package com.example.msreport.repository;

import com.example.msreport.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByTipoReporte(String tipoReporte);
    List<Report> findByGeneradoPorUserId(Long userId);
}
