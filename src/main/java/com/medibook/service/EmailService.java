package com.medibook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void enviarConfirmacionCita(String destinatario, String nombrePaciente,
                                       String nombreMedico, String fecha, String hora) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Confirmación de cita médica - MediBook");
        mensaje.setText(
                "Estimado/a " + nombrePaciente + ",\n\n" +
                        "Su cita médica ha sido confirmada con los siguientes detalles:\n\n" +
                        "Médico: " + nombreMedico + "\n" +
                        "Fecha: " + fecha + "\n" +
                        "Hora: " + hora + "\n\n" +
                        "Por favor llegue 10 minutos antes de su cita.\n\n" +
                        "Saludos,\nEquipo MediBook"
        );
        mailSender.send(mensaje);
    }

    @Async
    public void enviarCancelacionCita(String destinatario, String nombrePaciente,
                                      String nombreMedico, String fecha, String hora) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Cancelación de cita médica - MediBook");
        mensaje.setText(
                "Estimado/a " + nombrePaciente + ",\n\n" +
                        "Su cita médica ha sido cancelada:\n\n" +
                        "Médico: " + nombreMedico + "\n" +
                        "Fecha: " + fecha + "\n" +
                        "Hora: " + hora + "\n\n" +
                        "Si desea reagendar, ingrese a MediBook.\n\n" +
                        "Saludos,\nEquipo MediBook"
        );
        mailSender.send(mensaje);
    }

    @Async
    public void enviarReprogramacionCita(String destinatario, String nombrePaciente,
                                         String nombreMedico, String fecha, String hora) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Reprogramación de cita médica - MediBook");
        mensaje.setText(
                "Estimado/a " + nombrePaciente + ",\n\n" +
                        "Su cita médica ha sido reprogramada con los siguientes detalles:\n\n" +
                        "Médico: " + nombreMedico + "\n" +
                        "Nueva Fecha: " + fecha + "\n" +
                        "Nueva Hora: " + hora + "\n\n" +
                        "Por favor llegue 10 minutos antes de su cita.\n\n" +
                        "Saludos,\nEquipo MediBook"
        );
        mailSender.send(mensaje);
    }
}
