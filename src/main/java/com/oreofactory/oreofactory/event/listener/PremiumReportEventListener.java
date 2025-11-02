package com.oreofactory.oreofactory.event.listener;


import com.oreofactory.oreofactory.dto.request.PremiumReportRequestDTO;
import com.oreofactory.oreofactory.event.PremiumReportRequestedEvent;
import com.oreofactory.oreofactory.service.EmailService;
import com.oreofactory.oreofactory.service.SalesAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumReportEventListener {

    private final SalesAggregationService salesAggregationService;
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePremiumReportRequest(PremiumReportRequestedEvent event) {
        try {
            log.info("Procesando reporte premium para usuario: {}", event.getUsername());

            var request = event.getRequest();
            var aggregates = salesAggregationService.getAggregates(request.getFrom(), request.getTo(), request.getBranch());

            // Generar email HTML premium
            String htmlContent = generatePremiumHtmlEmail(aggregates, request);

            // Enviar email
            emailService.sendPremiumReport(
                    request.getEmailTo(),
                    "🍪 Reporte Semanal Premium - Oreo Factory",
                    htmlContent,
                    request.isAttachPdf() ? generatePdfReport(aggregates, request) : null
            );

            log.info("Reporte premium enviado exitosamente a: {}", request.getEmailTo());

        } catch (Exception e) {
            log.error("Error procesando reporte premium", e);
        }
    }

    private String generatePremiumHtmlEmail(SalesAggregationService.SalesAggregates aggregates,
                                            PremiumReportRequestDTO request) {
        String chartsHtml = "";

        if (request.isIncludeCharts()) {
            chartsHtml = generateChartsHtml(aggregates);
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reporte Semanal Oreo</title>
                <style>
                    body { 
                        font-family: 'Arial', sans-serif; 
                        margin: 0; 
                        padding: 0; 
                        background-color: #f5f5f5;
                    }
                    .container { 
                        max-width: 800px; 
                        margin: 0 auto; 
                        background: white;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #6B46C1, #805AD5); 
                        color: white; 
                        padding: 30px; 
                        text-align: center;
                    }
                    .header h1 { 
                        margin: 0; 
                        font-size: 28px;
                    }
                    .header .subtitle { 
                        opacity: 0.9; 
                        margin-top: 10px;
                    }
                    .content { 
                        padding: 30px; 
                    }
                    .metrics-grid { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
                        gap: 20px; 
                        margin: 30px 0;
                    }
                    .metric-card { 
                        background: #f8f9fa; 
                        padding: 20px; 
                        border-radius: 8px; 
                        text-align: center;
                        border-left: 4px solid #6B46C1;
                    }
                    .metric-value { 
                        font-size: 24px; 
                        font-weight: bold; 
                        color: #2D3748; 
                        margin: 10px 0;
                    }
                    .metric-label { 
                        color: #718096; 
                        font-size: 14px;
                    }
                    .chart-container { 
                        margin: 30px 0; 
                        text-align: center;
                    }
                    .chart-title { 
                        font-size: 18px; 
                        margin-bottom: 15px; 
                        color: #2D3748;
                    }
                    .footer { 
                        background: #EDF2F7; 
                        padding: 20px; 
                        text-align: center; 
                        color: #718096;
                        font-size: 12px;
                    }
                    .highlight { 
                        color: #6B46C1; 
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🍪 Reporte Semanal Premium Oreo Factory</h1>
                        <div class="subtitle">Período: %s a %s | Sucursal: %s</div>
                    </div>
                    
                    <div class="content">
                        <p>Estimado equipo gerencial,</p>
                        <p>Presentamos el reporte consolidado de ventas con un total de <span class="highlight">%,d unidades</span> vendidas y <span class="highlight">$%,.2f</span> en ingresos.</p>
                        
                        <div class="metrics-grid">
                            <div class="metric-card">
                                <div class="metric-value">%,d</div>
                                <div class="metric-label">Unidades Vendidas</div>
                            </div>
                            <div class="metric-card">
                                <div class="metric-value">$%,.2f</div>
                                <div class="metric-label">Ingresos Totales</div>
                            </div>
                            <div class="metric-card">
                                <div class="metric-value">%s</div>
                                <div class="metric-label">Producto Más Vendido</div>
                            </div>
                            <div class="metric-card">
                                <div class="metric-value">%s</div>
                                <div class="metric-label">Sucursal Líder</div>
                            </div>
                        </div>
                        
                        %s
                        
                        <p><strong>Análisis Destacado:</strong></p>
                        <p>%s lidera las preferencias de los clientes esta semana, representando el producto más popular. La sucursal %s demostró un desempeño excepcional en volumen de ventas.</p>
                        
                        <p>Saludos cordiales,<br>
                        <strong>Sistema de Reportes Oreo Factory</strong></p>
                    </div>
                    
                    <div class="footer">
                        Este es un reporte automático generado el %s. 
                        Los datos están sujetos a verificación.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                request.getFrom(),
                request.getTo(),
                request.getBranch() != null ? request.getBranch() : "Todas las sucursales",
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku(),
                aggregates.getTopBranch(),
                chartsHtml,
                aggregates.getTopSku(),
                aggregates.getTopBranch(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    private String generateChartsHtml(SalesAggregationService.SalesAggregates aggregates) {
        // Generar URLs para gráficos usando QuickChart.io
        String revenueChartUrl = generateRevenueChartUrl(aggregates);
        String unitsChartUrl = generateUnitsChartUrl(aggregates);

        return """
            <div class="chart-container">
                <div class="chart-title">Distribución de Ingresos por Sucursal</div>
                <img src="%s" alt="Gráfico de Ingresos" style="max-width: 100%%; height: auto;" />
            </div>
            <div class="chart-container">
                <div class="chart-title">Unidades Vendidas por Producto</div>
                <img src="%s" alt="Gráfico de Unidades" style="max-width: 100%%; height: auto;" />
            </div>
            """.formatted(revenueChartUrl, unitsChartUrl);
    }

    private String generateRevenueChartUrl(SalesAggregationService.SalesAggregates aggregates) {
        // Ejemplo simple - en producción deberías tener datos más detallados
        return "https://quickchart.io/chart?c=" +
                "{type:'bar',data:{labels:['Sucursal A','Sucursal B','Sucursal C']," +
                "datasets:[{label:'Ingresos',data:[2500,1800,3200],backgroundColor:['#6B46C1','#805AD5','#9F7AEA']}]}}";
    }

    private String generateUnitsChartUrl(SalesAggregationService.SalesAggregates aggregates) {
        return "https://quickchart.io/chart?c=" +
                "{type:'pie',data:{labels:['Oreo Classic','Oreo Chocolate','Oreo Golden']," +
                "datasets:[{data:[45,30,25],backgroundColor:['#6B46C1','#805AD5','#9F7AEA']}]}}";
    }

    private byte[] generatePdfReport(SalesAggregationService.SalesAggregates aggregates,
                                     PremiumReportRequestDTO request) {
        // Implementar generación de PDF (usando iText, Apache PDFBox, etc.)
        // Por ahora retornamos null - esto sería una implementación futura
        log.info("Generando PDF para reporte premium...");
        return null;
    }
}
