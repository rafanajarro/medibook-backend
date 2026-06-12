package com.medibook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EspecialidadRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
}
