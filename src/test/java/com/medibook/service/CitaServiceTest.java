package com.medibook.service;

import com.medibook.dto.CitaRequest;
import com.medibook.dto.CitaResponse;
import com.medibook.entity.*;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private HorarioDisponibleRepository horarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private CitaService citaService;

    private Paciente paciente;
    private Medico medico;
    private HorarioDisponible horario;
    private Usuario usuarioPaciente;
    private Usuario usuarioMedico;

    @BeforeEach
    void setUp() {
        usuarioPaciente = new Usuario();
        usuarioPaciente.setId(1L);
        usuarioPaciente.setNombre("Juan");
        usuarioPaciente.setApellido("Pérez");
        usuarioPaciente.setEmail("juan@mail.com");

        usuarioMedico = new Usuario();
        usuarioMedico.setId(2L);
        usuarioMedico.setNombre("Dr. Carlos");
        usuarioMedico.setApellido("García");
        usuarioMedico.setEmail("doctor@mail.com");

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setUsuario(usuarioPaciente);

        medico = new Medico();
        medico.setId(1L);
        medico.setUsuario(usuarioMedico);

        horario = new HorarioDisponible();
        horario.setId(1L);
        horario.setMedico(medico);
        horario.setFecha(LocalDate.now().plusDays(1));
        horario.setHoraInicio(LocalTime.of(9, 0));
        horario.setHoraFin(LocalTime.of(10, 0));
        horario.setDisponible(true);
    }

    @Test
    void reservar_exitoso() {
        CitaRequest request = new CitaRequest();
        request.setPacienteId(1L);
        request.setMedicoId(1L);
        request.setHorarioId(1L);
        request.setMotivo("Consulta general");

        Cita citaGuardada = new Cita();
        citaGuardada.setId(1L);
        citaGuardada.setPaciente(paciente);
        citaGuardada.setMedico(medico);
        citaGuardada.setHorario(horario);
        citaGuardada.setEstado("PENDIENTE");
        citaGuardada.setMotivo("Consulta general");

        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(citaRepository.existsByHorarioId(1L)).thenReturn(false);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaGuardada);

        CitaResponse response = citaService.reservar(request);

        assertNotNull(response);
        assertEquals("PENDIENTE", response.getEstado());
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(emailService, times(1)).enviarConfirmacionCita(any(), any(), any(), any(), any());
    }

    @Test
    void reservar_horarioNoDisponible_lanzaExcepcion() {
        horario.setDisponible(false);

        CitaRequest request = new CitaRequest();
        request.setHorarioId(1L);

        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        assertThrows(BadRequestException.class, () -> citaService.reservar(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void reservar_fechaPasada_lanzaExcepcion() {
        horario.setFecha(LocalDate.now().minusDays(1));

        CitaRequest request = new CitaRequest();
        request.setHorarioId(1L);

        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        assertThrows(BadRequestException.class, () -> citaService.reservar(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void cancelar_exitoso() {
        Cita cita = new Cita();
        cita.setId(1L);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setHorario(horario);
        cita.setEstado("PENDIENTE");

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        CitaResponse response = citaService.cancelar(1L);

        assertNotNull(response);
        assertEquals("CANCELADA", response.getEstado());
        verify(emailService, times(1)).enviarCancelacionCita(any(), any(), any(), any(), any());
    }

    @Test
    void cancelar_citaNoEncontrada_lanzaExcepcion() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> citaService.cancelar(999L));
    }
}