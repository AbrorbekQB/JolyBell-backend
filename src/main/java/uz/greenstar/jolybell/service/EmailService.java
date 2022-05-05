package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final Environment environment;

    public void sendEmail(String subject, String id, String receiver) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject(subject);
            helper.setText(Objects.requireNonNull(environment.getProperty("spring.mail.url")) + "/" + id);
            helper.setTo(receiver);
            helper.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
            javaMailSender.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}