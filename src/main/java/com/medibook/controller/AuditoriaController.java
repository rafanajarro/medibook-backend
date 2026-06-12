package com.medibook.controller;

import com.medibook.entity.Auditoria;
import com.medibook.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Auditoria>> listar() {
        return ResponseEntity.ok(auditoriaService.listar());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Auditoria>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/entidad/{entidad}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Auditoria>> listarPorEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }
}