package com.medibook.repository;

import com.medibook.entity.HorarioDisponible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HorarioDisponibleRepository extends JpaRepository<HorarioDisponible, Long> {
    List<HorarioDisponible> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);

    List<HorarioDisponible> findByMedicoIdAndDisponibleTrue(Long medicoId);

    List<HorarioDisponible> findByMedicoId(Long medicoId);
}
