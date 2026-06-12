package com.medibook.controller;

import com.medibook.dto.PacienteRequest;
import com.medibook.dto.PacienteResponse;
import com.medibook.service.AuditoriaService;
import com.medibook.service.PacienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PacienteController {
    private final PacienteService pacienteService;
    private final AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PacienteResponse>> listar() {
        return ResponseEntity.ok(pacienteService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<PacienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PacienteResponse> crear(@Valid @RequestBody PacienteRequest request,
                                                  HttpServletRequest httpRequest) {
        PacienteResponse response = pacienteService.crear(request);
        auditoriaService.registrar("CREAR", "PACIENTE", response.getId(),
                "Paciente creado: " + response.getNombre(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PACIENTE')")
    public ResponseEntity<PacienteResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody PacienteRequest request,
                                                       HttpServletRequest httpRequest) {
        PacienteResponse response = pacienteService.actualizar(id, request);
        auditoriaService.registrar("ACTUALIZAR", "PACIENTE", id,
                "Paciente actualizado: " + response.getNombre(),
                httpRequest.getRemoteAddr());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest httpRequest) {
        pacienteService.eliminar(id);
        auditoriaService.registrar("ELIMINAR", "PACIENTE", id,
                "Paciente eliminado id: " + id,
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
