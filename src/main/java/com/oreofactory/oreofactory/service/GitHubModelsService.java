package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.config.AIProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubModelsService {

    @Qualifier("AIProperties") // Especifica el bean correcto
    private final AIProperties aiProperties;
    private final RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public String generateSummary(SalesAggregationService.SalesAggregates aggregates) {
        try {
            String prompt = """
                    Con estos datos de ventas de Oreo: totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s.
                    Devuelve un resumen ejecutivo claro y conciso de máximo 120 palabras en español,
                    destacando los puntos más importantes para la gerencia."""
                    .formatted(aggregates.getTotalUnits(), aggregates.getTotalRevenue(),
                            aggregates.getTopSku(), aggregates.getTopBranch());

            Map<String, Object> requestBody = createRequestBody(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiProperties.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Calling AI endpoint: {} with model: {}",
                    aiProperties.getEndpoint(), aiProperties.getDefaultModel());

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    aiProperties.getEndpoint(), HttpMethod.POST, entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String summary = extractContentFromResponse(response.getBody());
                return validateAndTrimSummary(summary);
            } else {
                log.error("AI API returned error status: {}", response.getStatusCode());
                throw new RuntimeException("AI service unavailable: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error calling AI API", e);
            return generateFallbackSummary(aggregates);
        }
    }

    private Map<String, Object> createRequestBody(String prompt) {
        return Map.of(
                "model", aiProperties.getDefaultModel(),
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                        Eres un analista de negocios experto que escribe resúmenes ejecutivos breves,
                                        claros y accionables para emails corporativos. Usa un tono profesional pero accesible."""
                        ),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 200,
                "temperature", 0.7
        );
    }

    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.getFirst();
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }

            if (response.containsKey("content")) {
                return (String) response.get("content");
            }

        } catch (Exception e) {
            log.error("Error extracting content from AI response", e);
        }

        return "No se pudo generar el resumen. Por favor contacte al administrador.";
    }

    private String validateAndTrimSummary(String summary) {
        if (summary == null || summary.trim().isEmpty()) {
            return "El resumen generado está vacío.";
        }

        String[] words = summary.split("\\s+");
        if (words.length > 120) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = 0; i < 120; i++) {
                trimmed.append(words[i]).append(" ");
            }
            return trimmed.toString().trim() + "... [resumen truncado]";
        }

        return summary;
    }

    private String generateFallbackSummary(SalesAggregationService.SalesAggregates aggregates) {
        return """
                **Resumen Semanal de Ventas Oreo**
                
                **Resultados Destacados:**
                • Unidades vendidas: %d unidades
                • Ingresos totales: $%.2f
                • Producto más popular: %s
                • Sucursal líder: %s
                
                **Análisis:** Las ventas muestran un desempeño sólido esta semana,
                con %s liderando las preferencias de los clientes. La sucursal %s
                destacó como la más productiva. Se recomienda mantener el inventario
                adecuado del SKU más vendido y replicar las mejores prácticas de la sucursal líder.
                
                Este es un resumen automático generado por el sistema."""
                .formatted(
                        aggregates.getTotalUnits(),
                        aggregates.getTotalRevenue(),
                        aggregates.getTopSku(),
                        aggregates.getTopBranch(),
                        aggregates.getTopSku(),
                        aggregates.getTopBranch()
                );
    }
}