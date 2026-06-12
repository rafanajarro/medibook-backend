package com.medibook.repository;

import com.medibook.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Boolean existsByNombre(String nombre);
}
