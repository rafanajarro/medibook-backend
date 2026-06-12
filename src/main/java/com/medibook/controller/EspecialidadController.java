package com.medibook.controller;

import com.medibook.dto.EspecialidadRequest;
import com.medibook.entity.Especialidad;
import com.medibook.service.AuditoriaService;
import com.medibook.service.EspecialidadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {
    private final EspecialidadService especialidadService;
    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(especialidadService.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Especialidad> crear(@Valid @RequestBody EspecialidadRequest request,
                                              HttpServletRequest httpRequest) {
        Especialidad response = especialidadService.crear(request);
        auditoriaService.registrar("CREAR", "ESPECIALIDAD", response.getId(),
                "Especialidad creada: " + response.getNombre(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Especialidad> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody EspecialidadRequest request,
                                                   HttpServletRequest httpRequest) {
        Especialidad response = especialidadService.actualizar(id, request);
        auditoriaService.registrar("ACTUALIZAR", "ESPECIALIDAD", id,
                "Especialidad actualizada: " + response.getNombre(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest httpRequest) {
        especialidadService.eliminar(id);
        auditoriaService.registrar("ELIMINAR", "ESPECIALIDAD", id,
                "Especialidad eliminada id: " + id,
                httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
