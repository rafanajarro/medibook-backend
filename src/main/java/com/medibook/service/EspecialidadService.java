package com.medibook.service;

import com.medibook.dto.EspecialidadRequest;
import com.medibook.entity.Especialidad;
import com.medibook.exception.BadRequestException;
import com.medibook.exception.ResourceNotFoundException;
import com.medibook.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadService {
    private final EspecialidadRepository especialidadRepository;

    public List<Especialidad> listar() {
        return especialidadRepository.findAll();
    }

    public Especialidad crear(EspecialidadRequest request) {
        if (especialidadRepository.existsByNombre(request.getNombre())) {
            throw new BadRequestException("La especialidad ya existe");
        }

        Especialidad especialidad = new Especialidad();
        especialidad.setNombre(request.getNombre());
        especialidad.setDescripcion(request.getDescripcion());

        return especialidadRepository.save(especialidad);
    }

    public Especialidad actualizar(Long id, EspecialidadRequest request) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        especialidad.setNombre(request.getNombre());
        especialidad.setDescripcion(request.getDescripcion());

        return especialidadRepository.save(especialidad);
    }

    public void eliminar(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        especialidadRepository.delete(especialidad);
    }
}
