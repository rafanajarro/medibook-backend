package com.medibook.service;

import com.medibook.dto.MedicoRequest;
import com.medibook.dto.MedicoResponse;
import com.medibook.entity.Especialidad;
import com.medibook.entity.Medico;
import com.medibook.entity.Usuario;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.EspecialidadRepository;
import com.medibook.repository.MedicoRepository;
import com.medibook.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicoService {
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;

    public List<MedicoResponse> listar() {
        return medicoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MedicoResponse obtener(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
        return toResponse(medico);
    }

    public MedicoResponse crear(MedicoRequest request) {
        if (medicoRepository.existsByNumeroLicencia(request.getNumeroLicencia())) {
            throw new BadRequestException("El número de licencia ya existe");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<Especialidad> especialidades = new HashSet<>();
        if (request.getEspecialidadIds() != null) {
            especialidades = new HashSet<>(especialidadRepository.findAllById(request.getEspecialidadIds()));
        }

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setNumeroLicencia(request.getNumeroLicencia());
        medico.setTelefono(request.getTelefono());
        medico.setEspecialidades(especialidades);

        return toResponse(medicoRepository.save(medico));
    }

    public MedicoResponse actualizar(Long id, MedicoRequest request) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        Set<Especialidad> especialidades = new HashSet<>();
        if (request.getEspecialidadIds() != null) {
            especialidades = new HashSet<>(especialidadRepository.findAllById(request.getEspecialidadIds()));
        }

        medico.setNumeroLicencia(request.getNumeroLicencia());
        medico.setTelefono(request.getTelefono());
        medico.setEspecialidades(especialidades);

        return toResponse(medicoRepository.save(medico));
    }

    public void eliminar(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
        medicoRepository.delete(medico);
    }

    private MedicoResponse toResponse(Medico medico) {
        MedicoResponse response = new MedicoResponse();
        response.setId(medico.getId());
        response.setNombre(medico.getUsuario().getNombre());
        response.setApellido(medico.getUsuario().getApellido());
        response.setEmail(medico.getUsuario().getEmail());
        response.setNumeroLicencia(medico.getNumeroLicencia());
        response.setTelefono(medico.getTelefono());
        response.setActivo(medico.getActivo());
        response.setEspecialidades(medico.getEspecialidades().stream()
                .map(Especialidad::getNombre)
                .collect(Collectors.toSet()));
        return response;
    }
}
