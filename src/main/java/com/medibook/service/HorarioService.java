package com.medibook.service;

import com.medibook.dto.HorarioRequest;
import com.medibook.dto.HorarioResponse;
import com.medibook.entity.HorarioDisponible;
import com.medibook.entity.Medico;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.HorarioDisponibleRepository;
import com.medibook.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioService {
    private final HorarioDisponibleRepository horarioRepository;
    private final MedicoRepository medicoRepository;
    private final NotificacionService notificacionService;

    public List<HorarioResponse> listarPorMedico(Long medicoId) {
        return horarioRepository.findByMedicoId(medicoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<HorarioResponse> listarDisponiblesPorMedico(Long medicoId) {
        return horarioRepository.findByMedicoIdAndDisponibleTrue(medicoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HorarioResponse crear(HorarioRequest request) {
        if (request.getHoraInicio().isAfter(request.getHoraFin())) {
            throw new BadRequestException("La hora de inicio no puede ser después de la hora de fin");
        }

        if (request.getFecha().isBefore(LocalDate.now())) {
            throw new BadRequestException("No se pueden crear horarios en fechas pasadas");
        }

        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        HorarioDisponible horario = new HorarioDisponible();
        horario.setMedico(medico);
        horario.setFecha(request.getFecha());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());

        HorarioResponse response = toResponse(horarioRepository.save(horario));
        notificacionService.notificarHorarioActualizado(horario.getMedico().getId(), "CREADO");
        return response;
    }

    public void eliminar(Long id) {
        HorarioDisponible horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));
        notificacionService.notificarHorarioActualizado(horario.getMedico().getId(), "ELIMINADO");
        horarioRepository.delete(horario);
    }

    private HorarioResponse toResponse(HorarioDisponible horario) {
        HorarioResponse response = new HorarioResponse();
        response.setId(horario.getId());
        response.setMedicoId(horario.getMedico().getId());
        response.setNombreMedico(horario.getMedico().getUsuario().getNombre()
                + " " + horario.getMedico().getUsuario().getApellido());
        response.setFecha(horario.getFecha());
        response.setHoraInicio(horario.getHoraInicio());
        response.setHoraFin(horario.getHoraFin());
        response.setDisponible(horario.getDisponible());
        return response;
    }
}
