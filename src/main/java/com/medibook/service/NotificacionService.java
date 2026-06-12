package com.medibook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarHorarioActualizado(Long medicoId, String tipo) {
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("tipo", tipo);
        mensaje.put("medicoId", medicoId);
        mensaje.put("mensaje", "Horario actualizado");

        String destino = "/topic/horarios/" + medicoId;
        messagingTemplate.convertAndSend(destino, (Object) mensaje);
    }

    public void notificarCitaActualizada(Long pacienteId, String tipo) {
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("tipo", tipo);
        mensaje.put("pacienteId", pacienteId);
        mensaje.put("mensaje", "Cita actualizada");

        String destino = "/topic/citas/" + pacienteId;
        messagingTemplate.convertAndSend(destino, (Object) mensaje);
    }
}