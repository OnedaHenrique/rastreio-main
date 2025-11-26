package com.sd.rastreio.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia um e-mail simples.
     *
     * @param para    Destinatário do e-mail
     * @param assunto Assunto do e-mail
     * @param texto   Corpo do e-mail
     */
    public void enviarEmail(String para, String assunto, String texto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(para);
            message.setSubject(assunto);
            message.setText(texto);

            // Remetente configurável
            message.setFrom("aline.sabel7@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email enviado com sucesso para " + para);

        } catch (Exception e) {
            System.err.println("❌ Falha ao enviar email para " + para + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    
}
