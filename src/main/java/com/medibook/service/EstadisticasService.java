package com.medibook.service;

import com.medibook.dto.EstadisticasResponse;
import com.medibook.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;
    private final EspecialidadRepository especialidadRepository;

    public EstadisticasResponse obtener() {
        EstadisticasResponse response = new EstadisticasResponse();

        response.setTotalPacientes(pacienteRepository.count());
        response.setTotalMedicos(medicoRepository.count());
        response.setTotalEspecialidades(especialidadRepository.count());

        var citas = citaRepository.findAll();
        response.setTotalCitas(citas.size());
        response.setCitasPendientes(citas.stream().filter(c -> c.getEstado().equals("PENDIENTE")).count());
        response.setCitasConfirmadas(citas.stream().filter(c -> c.getEstado().equals("CONFIRMADA")).count());
        response.setCitasCanceladas(citas.stream().filter(c -> c.getEstado().equals("CANCELADA")).count());
        response.setCitasCompletadas(citas.stream().filter(c -> c.getEstado().equals("COMPLETADA")).count());

        return response;
    }
}