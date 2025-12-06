package org.example.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.api.exception.ActivationMailException;
import org.example.api.exception.ResetEmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${frontend.application.url}")
    private String frontendApplicationUrl;

    public void sendActivationEmail(String to, String firstName, String token) {
        String subject = "Potwierdzenie rejestracji w Restauracji";
        String activationUrl = frontendApplicationUrl + "/activate?token=" + token;

        String htmlContent = """
                <html>
                    <body>
                        <h1>Witaj %s!</h1>
                        <p>Dziękujemy za rejestrację. Kliknij poniższy link, aby aktywować konto:</p>
                        <a href="%s">AKTYWUJ KONTO</a>
                        <p>Link jest ważny przez 24 godziny.</p>
                    </body>
                </html>
                """.formatted(firstName, activationUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = html
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ActivationMailException("Nie udało się wysłać maila aktywacyjnego");
        }
    }


    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Resetowanie hasła";
        String resetUrl = frontendApplicationUrl + "/reset-password?token=" + token;

        String htmlContent = """
                <html>
                    <body>
                        <h1>Zapomniałeś hasła?</h1>
                        <p>Kliknij poniższy link, aby ustawić nowe hasło:</p>
                        <a href="%s">RESETUJ HASŁO</a>
                        <p>Link jest ważny przez 15 minut.</p>
                        <p>Jeśli to nie Ty prosiłeś o reset, zignoruj tę wiadomość.</p>
                    </body>
                </html>
                """.formatted(resetUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ResetEmailException("Nie udało się wysłać maila resetującego hasło");
        }
    }
}