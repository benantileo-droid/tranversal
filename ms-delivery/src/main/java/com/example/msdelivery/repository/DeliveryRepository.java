package com.example.msdelivery.repository;

import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.model.Delivery.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByEstado(Estado estado);
    List<Delivery> findByReservationId(Long reservationId);
}
