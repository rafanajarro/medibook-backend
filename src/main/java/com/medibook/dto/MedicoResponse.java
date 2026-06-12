package com.medibook.dto;

import lombok.Data;

import java.util.Set;

@Data
public class MedicoResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String numeroLicencia;
    private String telefono;
    private Boolean activo;
    private Set<String> especialidades;
}
