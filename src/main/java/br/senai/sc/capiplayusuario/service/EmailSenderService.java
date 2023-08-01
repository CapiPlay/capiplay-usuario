package br.senai.sc.capiplayusuario.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSenderService {
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("henrique_lazzarino@estudante.sesisenai.org.br");
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Use true para indicar que o conteúdo é HTML
            helper.setText(body, true);

            javaMailSender.send(message);

            System.out.println("Email enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
