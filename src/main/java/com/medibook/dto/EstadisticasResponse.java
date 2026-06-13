package com.medibook.dto;

import lombok.Data;

@Data
public class EstadisticasResponse {
    private long totalPacientes;
    private long totalMedicos;
    private long totalCitas;
    private long citasPendientes;
    private long citasConfirmadas;
    private long citasCanceladas;
    private long citasCompletadas;
    private long totalEspecialidades;
}