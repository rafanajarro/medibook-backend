package com.medibook.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CitaRequest {
    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "El horario es obligatorio")
    private Long horarioId;

    private String motivo;
}
