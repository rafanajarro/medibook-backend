package com.medibook.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class HorarioResponse {
    private Long id;
    private Long medicoId;
    private String nombreMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean disponible;
}
