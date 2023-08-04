package br.senai.sc.capiplayusuario.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSenderService {
    private JavaMailSender javaMailSender;
    private Environment environment;

    public void validEmail(String toEmail, String subject) {
        try {
            String verificationLink = createVerificationLink();

            // Load the HTML file from the static folder
            String htmlContent = "<html><body><h1>Email Verification</h1><p>Clique <a href='" + verificationLink + "'>aqui</a> para verificar o email:</p></body></html>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("henrique.a.lazzarino@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Use true para indicar que o conteúdo é HTML
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

            System.out.println("Email enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String createVerificationLink() {
        String baseUrl = environment.getProperty("app.baseurl"); // Set the base URL of your application
        return baseUrl + "/verify.html"; // Adjust the URL path accordingly
    }
}
