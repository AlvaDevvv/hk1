package com.oreofactory.oreofactory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendReportEmail(String to, String summary, SalesAggregationService.SalesAggregates aggregates) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Resumen Semanal de Ventas Oreo");
            message.setText(createEmailContent(summary, aggregates));

            mailSender.send(message);
            log.info("Report email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send report email to: {}", to, e);
            throw new RuntimeException("Email service unavailable", e);
        }
    }

    private String createEmailContent(String summary, SalesAggregationService.SalesAggregates aggregates) {
        return String.format(
                "RESUMEN SEMANAL DE VENTAS OREO\n\n" +
                        "DATOS PRINCIPALES:\n" +
                        "• Total de unidades: %d\n" +
                        "• Ingresos totales: $%.2f\n" +
                        "• SKU más vendido: %s\n" +
                        "• Sucursal líder: %s\n\n" +
                        "ANÁLISIS GENERADO POR IA:\n%s\n\n" +
                        "Este es un reporte automático generado por el Sistema de Ventas Oreo.",
                aggregates.getTotalUnits(), aggregates.getTotalRevenue(), aggregates.getTopSku(), aggregates.getTopBranch(), summary
        );
    }
}
