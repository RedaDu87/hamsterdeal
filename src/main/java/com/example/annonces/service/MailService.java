package com.example.annonces.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeMail(String to, String firstName) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom("contact@hamsterdeal.ch");
            helper.setTo(to);
            helper.setSubject("Bienvenue sur HamsterDeal 🚀");

            String html = """
                    <div style="font-family:Arial,sans-serif">
                      <h2>Bienvenue %s 👋</h2>
                      <p>Merci pour ton inscription sur <b>HamsterDeal</b>.</p>
                      <p>Tu peux maintenant publier, consulter et gérer tes annonces en toute simplicité.</p>
                      <p>Si tu as une question, réponds simplement à ce mail.</p>
                      <br>
                      <p>À bientôt,<br>
                      <b>L'équipe HamsterDeal</b></p>
                    </div>
                    """.formatted(firstName);

            helper.setText(html, true);

            mailSender.send(msg);

        } catch (Exception e) {
            // on ne bloque pas l'inscription si le mail échoue
            e.printStackTrace();
        }
    }
}
