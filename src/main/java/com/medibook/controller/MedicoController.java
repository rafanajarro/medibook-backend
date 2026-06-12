package com.medibook.controller;

import com.medibook.dto.MedicoRequest;
import com.medibook.dto.MedicoResponse;
import com.medibook.service.AuditoriaService;
import com.medibook.service.MedicoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {
    private final MedicoService medicoService;
    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<MedicoResponse>> listar() {
        return ResponseEntity.ok(medicoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoResponse> crear(@Valid @RequestBody MedicoRequest request,
                                                HttpServletRequest httpRequest) {
        MedicoResponse response = medicoService.crear(request);
        auditoriaService.registrar("CREAR", "MEDICO", response.getId(),
                "Médico creado: " + response.getNombre(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody MedicoRequest request,
                                                     HttpServletRequest httpRequest) {
        MedicoResponse response = medicoService.actualizar(id, request);
        auditoriaService.registrar("ACTUALIZAR", "MEDICO", id,
                "Médico actualizado: " + response.getNombre(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest httpRequest) {
        medicoService.eliminar(id);
        auditoriaService.registrar("ELIMINAR", "MEDICO", id,
                "Médico eliminado id: " + id,
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
