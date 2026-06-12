package com.medibook.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class CitaResponse {
    private Long id;
    private String nombrePaciente;
    private String nombreMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String motivo;
    private String notas;
    private LocalDateTime createdAt;
}
