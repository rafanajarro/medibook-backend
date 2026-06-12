package com.medibook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class MedicoRequest {
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String numeroLicencia;

    private String telefono;

    private Set<Long> especialidadIds;
}
