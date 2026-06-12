package com.medibook.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String direccion;
}
