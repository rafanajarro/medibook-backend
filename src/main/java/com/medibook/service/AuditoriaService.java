package com.medibook.service;

import com.medibook.entity.Auditoria;
import com.medibook.entity.Usuario;
import com.medibook.repository.AuditoriaRepository;
import com.medibook.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public void registrar(String accion, String entidad, Long entidadId, String detalle, String ipAddress) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            Auditoria auditoria = new Auditoria();
            auditoria.setUsuario(usuario);
            auditoria.setAccion(accion);
            auditoria.setEntidad(entidad);
            auditoria.setEntidadId(entidadId);
            auditoria.setDetalle(detalle);
            auditoria.setIpAddress(ipAddress);

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // Si falla la auditoría no debe afectar la operación principal
        }
    }

    public List<Auditoria> listar() {
        return auditoriaRepository.findAll();
    }

    public List<Auditoria> listarPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Auditoria> listarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad);
    }
}