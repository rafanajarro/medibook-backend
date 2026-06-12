package com.medibook.service;

import com.medibook.dto.UsuarioResponse;
import com.medibook.entity.Usuario;
import com.medibook.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        return response;
    }
}
