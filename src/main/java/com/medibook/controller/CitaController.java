package com.medibook.controller;

import com.medibook.dto.CitaRequest;
import com.medibook.dto.CitaResponse;
import com.medibook.service.AuditoriaService;
import com.medibook.service.CitaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaService citaService;
    private final AuditoriaService auditoriaService;

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<List<CitaResponse>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<List<CitaResponse>> listarPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.listarPorMedico(medicoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PACIENTE')")
    public ResponseEntity<CitaResponse> reservar(@Valid @RequestBody CitaRequest request,
                                                 HttpServletRequest httpRequest) {
        CitaResponse response = citaService.reservar(request);
        auditoriaService.registrar("CREAR", "CITA", response.getId(),
                "Cita reservada para " + response.getNombrePaciente(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<CitaResponse> cancelar(@PathVariable Long id,
                                                 HttpServletRequest httpRequest) {
        CitaResponse response = citaService.cancelar(id);
        auditoriaService.registrar("CANCELAR", "CITA", id,
                "Cita cancelada para " + response.getNombrePaciente(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reprogramar/{nuevoHorarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PACIENTE')")
    public ResponseEntity<CitaResponse> reprogramar(@PathVariable Long id,
                                                    @PathVariable Long nuevoHorarioId,
                                                    HttpServletRequest httpRequest) {
        CitaResponse response = citaService.reprogramar(id, nuevoHorarioId);
        auditoriaService.registrar("REPROGRAMAR", "CITA", id,
                "Cita reprogramada para " + response.getNombrePaciente(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }
}