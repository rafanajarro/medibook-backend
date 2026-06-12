package com.medibook.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequest {
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    private LocalDate fechaNacimiento;
    private String telefono;
    private String direccion;
}
