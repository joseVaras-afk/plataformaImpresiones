package com.impresiones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }
    
}
