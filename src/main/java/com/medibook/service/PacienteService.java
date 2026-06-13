package com.medibook.service;

import com.medibook.dto.PacienteRequest;
import com.medibook.dto.PacienteResponse;
import com.medibook.entity.Paciente;
import com.medibook.entity.Usuario;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.PacienteRepository;
import com.medibook.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PacienteResponse obtener(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        return toResponse(paciente);
    }

    public PacienteResponse crear(PacienteRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setTelefono(request.getTelefono());
        paciente.setDireccion(request.getDireccion());

        return toResponse(pacienteRepository.save(paciente));
    }

    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setTelefono(request.getTelefono());
        paciente.setDireccion(request.getDireccion());

        return toResponse(pacienteRepository.save(paciente));
    }

    public void eliminar(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        pacienteRepository.delete(paciente);
    }

    private PacienteResponse toResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();
        response.setId(paciente.getId());
        response.setNombre(paciente.getUsuario().getNombre());
        response.setApellido(paciente.getUsuario().getApellido());
        response.setEmail(paciente.getUsuario().getEmail());
        response.setFechaNacimiento(paciente.getFechaNacimiento());
        response.setTelefono(paciente.getTelefono());
        response.setDireccion(paciente.getDireccion());
        return response;
    }

    public PacienteResponse obtenerPorEmail(String email) {
        Paciente paciente = pacienteRepository.findAll().stream()
                .filter(p -> p.getUsuario().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        return toResponse(paciente);
    }
}
