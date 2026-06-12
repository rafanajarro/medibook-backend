package com.medibook.controller;

import com.medibook.dto.HorarioRequest;
import com.medibook.dto.HorarioResponse;
import com.medibook.service.AuditoriaService;
import com.medibook.service.HorarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioController {
    private final HorarioService horarioService;
    private final AuditoriaService auditoriaService;

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<HorarioResponse>> listarPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(horarioService.listarPorMedico(medicoId));
    }

    @GetMapping("/medico/{medicoId}/disponibles")
    public ResponseEntity<List<HorarioResponse>> listarDisponibles(@PathVariable Long medicoId) {
        return ResponseEntity.ok(horarioService.listarDisponiblesPorMedico(medicoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<HorarioResponse> crear(@Valid @RequestBody HorarioRequest request,
                                                 HttpServletRequest httpRequest) {
        HorarioResponse response = horarioService.crear(request);
        auditoriaService.registrar("CREAR", "HORARIO", response.getId(),
                "Horario creado para médico id: " + response.getMedicoId(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest httpRequest) {
        horarioService.eliminar(id);
        auditoriaService.registrar("ELIMINAR", "HORARIO", id,
                "Horario eliminado id: " + id,
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
