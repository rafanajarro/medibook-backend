package com.medibook.service;

import com.medibook.dto.CitaRequest;
import com.medibook.dto.CitaResponse;
import com.medibook.entity.Cita;
import com.medibook.entity.HorarioDisponible;
import com.medibook.entity.Medico;
import com.medibook.entity.Paciente;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.CitaRepository;
import com.medibook.repository.HorarioDisponibleRepository;
import com.medibook.repository.MedicoRepository;
import com.medibook.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {
    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final HorarioDisponibleRepository horarioRepository;
    private final EmailService emailService;
    private final NotificacionService notificacionService;

    public List<CitaResponse> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> listarPorMedico(Long medicoId) {
        return citaRepository.findByMedicoId(medicoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CitaResponse reservar(CitaRequest request) {
        HorarioDisponible horario = horarioRepository.findById(request.getHorarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        if (!horario.getDisponible()) {
            throw new BadRequestException("El horario ya no está disponible");
        }

        if (horario.getFecha().isBefore(LocalDate.now())) {
            throw new BadRequestException("No se pueden reservar citas en fechas pasadas");
        }

        if (citaRepository.existsByHorarioId(request.getHorarioId())) {
            throw new BadRequestException("Ya existe una cita en ese horario");
        }

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        horario.setDisponible(false);
        horarioRepository.save(horario);

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setHorario(horario);
        cita.setMotivo(request.getMotivo());
        cita.setEstado("PENDIENTE");

        CitaResponse response = toResponse(citaRepository.save(cita));

        emailService.enviarConfirmacionCita(
                cita.getPaciente().getUsuario().getEmail(),
                cita.getPaciente().getUsuario().getNombre(),
                cita.getMedico().getUsuario().getNombre() + " " + cita.getMedico().getUsuario().getApellido(),
                cita.getHorario().getFecha().toString(),
                cita.getHorario().getHoraInicio().toString()
        );

        notificacionService.notificarHorarioActualizado(cita.getMedico().getId(), "RESERVADO");
        notificacionService.notificarCitaActualizada(cita.getPaciente().getId(), "CREADA");

        return response;
    }

    public CitaResponse cancelar(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (cita.getEstado().equals("CANCELADA")) {
            throw new BadRequestException("La cita ya está cancelada");
        }

        HorarioDisponible horario = cita.getHorario();
        horario.setDisponible(true);
        horarioRepository.save(horario);

        cita.setEstado("CANCELADA");
        CitaResponse response = toResponse(citaRepository.save(cita));

        emailService.enviarCancelacionCita(
                cita.getPaciente().getUsuario().getEmail(),
                cita.getPaciente().getUsuario().getNombre(),
                cita.getMedico().getUsuario().getNombre() + " " + cita.getMedico().getUsuario().getApellido(),
                cita.getHorario().getFecha().toString(),
                cita.getHorario().getHoraInicio().toString()
        );

        notificacionService.notificarHorarioActualizado(cita.getMedico().getId(), "CANCELADO");
        notificacionService.notificarCitaActualizada(cita.getPaciente().getId(), "CANCELADA");

        return response;
    }

    public CitaResponse reprogramar(Long id, Long nuevoHorarioId) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        HorarioDisponible nuevoHorario = horarioRepository.findById(nuevoHorarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        if (!nuevoHorario.getDisponible()) {
            throw new BadRequestException("El nuevo horario no está disponible");
        }

        HorarioDisponible horarioAnterior = cita.getHorario();
        horarioAnterior.setDisponible(true);
        horarioRepository.save(horarioAnterior);

        nuevoHorario.setDisponible(false);
        horarioRepository.save(nuevoHorario);

        cita.setHorario(nuevoHorario);
        cita.setEstado("PENDIENTE");
        CitaResponse response = toResponse(citaRepository.save(cita));

        emailService.enviarReprogramacionCita(
                cita.getPaciente().getUsuario().getEmail(),
                cita.getPaciente().getUsuario().getNombre(),
                cita.getMedico().getUsuario().getNombre() + " " + cita.getMedico().getUsuario().getApellido(),
                nuevoHorario.getFecha().toString(),
                nuevoHorario.getHoraInicio().toString()
        );

        notificacionService.notificarHorarioActualizado(cita.getMedico().getId(), "REPROGRAMADO");
        notificacionService.notificarCitaActualizada(cita.getPaciente().getId(), "REPROGRAMADA");

        return response;
    }


    private CitaResponse toResponse(Cita cita) {
        CitaResponse response = new CitaResponse();
        response.setId(cita.getId());
        response.setNombrePaciente(cita.getPaciente().getUsuario().getNombre()
                + " " + cita.getPaciente().getUsuario().getApellido());
        response.setNombreMedico(cita.getMedico().getUsuario().getNombre()
                + " " + cita.getMedico().getUsuario().getApellido());
        response.setFecha(cita.getHorario().getFecha());
        response.setHoraInicio(cita.getHorario().getHoraInicio());
        response.setHoraFin(cita.getHorario().getHoraFin());
        response.setEstado(cita.getEstado());
        response.setMotivo(cita.getMotivo());
        response.setNotas(cita.getNotas());
        response.setCreatedAt(cita.getCreatedAt());
        return response;
    }
}
