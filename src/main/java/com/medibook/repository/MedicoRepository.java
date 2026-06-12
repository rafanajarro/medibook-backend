package com.medibook.repository;

import com.medibook.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByUsuarioId(Long usuarioId);

    Boolean existsByNumeroLicencia(String numeroLicencia);
}
