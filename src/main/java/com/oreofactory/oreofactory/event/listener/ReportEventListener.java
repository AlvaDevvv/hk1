package com.oreofactory.oreofactory.event.listener;

import com.oreofactory.oreofactory.event.ReportRequestedEvent;
import com.oreofactory.oreofactory.model.entity.Sale;
import com.oreofactory.oreofactory.repository.SaleRepository;
import com.oreofactory.oreofactory.service.EmailService;
import com.oreofactory.oreofactory.service.GitHubModelsService;
import com.oreofactory.oreofactory.service.SalesAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final SaleRepository saleRepository;
    private final SalesAggregationService salesAggregationService;
    private final GitHubModelsService gitHubModelsService;
    private final EmailService emailService;

    @Async
    @TransactionalEventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        try {
            log.info("Processing report request for period: {} to {}", event.getStartDate(), event.getEndDate());

            // 1. Calcular agregados de ventas en el rango
            LocalDateTime start = event.getStartDate().atStartOfDay();
            LocalDateTime end = event.getEndDate().atTime(LocalTime.MAX);

            List<Sale> sales = event.getBranch() != null ?
                    saleRepository.findByBranchAndSoldAtBetween(event.getBranch(), start, end) :
                    saleRepository.findBySoldAtBetween(start, end);

            SalesAggregationService.SalesAggregates aggregates =
                    salesAggregationService.calculateAggregates(sales);

            // 2. Construir y enviar prompt a GitHub Models
            String summary = gitHubModelsService.generateSummary(aggregates);

            // 3. Validar respuesta del LLM (≤120 palabras)
            if (summary.split("\\s+").length > 120) {
                summary = summary.substring(0, Math.min(summary.length(), 500)) + "... [truncated]";
            }

            // 4. Enviar email con formato profesional
            emailService.sendReportEmail(event.getEmail(), summary, aggregates);

            log.info("Report successfully processed and sent to: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Error processing report request", e);
            // Podríamos enviar un email de error aquí
        }
    }
}
