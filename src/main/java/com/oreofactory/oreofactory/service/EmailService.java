package com.oreofactory.oreofactory.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPremiumReport(String to, String subject, String htmlContent, byte[] pdfAttachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content

            if (pdfAttachment != null) {
                helper.addAttachment("reporte_oreo_premium.pdf",
                        new ByteArrayResource(pdfAttachment), "application/pdf");
            }

            mailSender.send(message);
            log.info("Email premium enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            log.error("Error enviando email premium a: {}", to, e);
            throw new RuntimeException("Error enviando email premium", e);
        }
    }

    // Mantener el método existente para emails simples
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false = texto plano

            mailSender.send(message);
            log.info("Email simple enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            log.error("Error enviando email simple a: {}", to, e);
            throw new RuntimeException("Error enviando email", e);
        }
    }
}