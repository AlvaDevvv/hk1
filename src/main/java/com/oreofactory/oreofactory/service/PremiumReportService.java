package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.dto.request.PremiumReportRequestDTO;
import com.oreofactory.oreofactory.dto.response.PremiumReportResponseDTO;
import com.oreofactory.oreofactory.event.PremiumReportRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumReportService {

    private final ApplicationEventPublisher eventPublisher;

    public PremiumReportResponseDTO generatePremiumReport(PremiumReportRequestDTO request, String username) {
        String requestId = "req_premium_" + UUID.randomUUID().toString().substring(0, 8);

        log.info("Solicitando reporte premium: {} para usuario: {}", requestId, username);

        // Publicar evento para procesamiento asíncrono
        eventPublisher.publishEvent(new PremiumReportRequestedEvent(this, request, username));

        List<String> features = Arrays.asList("HTML_FORMAT");
        if (request.isIncludeCharts()) {
            features.add("CHARTS");
        }
        if (request.isAttachPdf()) {
            features.add("PDF_ATTACHMENT");
        }

        return PremiumReportResponseDTO.builder()
                .requestId(requestId)
                .status("PROCESSING")
                .message("Su reporte premium está siendo generado. Incluirá gráficos y PDF adjunto.")
                .estimatedTime("60-90 segundos")
                .features(features)
                .build();
    }
}