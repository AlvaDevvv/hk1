package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.model.entity.Sale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        // Given ventas de prueba
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );

        // When calcular agregados
        SalesAggregationService.SalesAggregates result = salesAggregationService.calculateAggregates(mockSales);

        // Then verificar totalUnits=30, totalRevenue=42.43, topSku="OREO_CLASSIC", topBranch="Miraflores"
        assertThat(result.getTotalUnits()).isEqualTo(30);
        assertThat(result.getTotalRevenue()).isEqualTo(42.43);
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    @Test
    void shouldHandleEmptySalesList() {
        // Given lista vacía
        List<Sale> emptySales = List.of();

        // When calcular agregados
        SalesAggregationService.SalesAggregates result = salesAggregationService.calculateAggregates(emptySales);

        // Then valores por defecto
        assertThat(result.getTotalUnits()).isEqualTo(0);
        assertThat(result.getTotalRevenue()).isEqualTo(0.0);
        assertThat(result.getTopSku()).isEqualTo("N/A");
        assertThat(result.getTopBranch()).isEqualTo("N/A");
    }

    @Test
    void shouldFilterByBranch() {
        // Given ventas de múltiples sucursales
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 20, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 5, 1.99, "Miraflores")
        );

        // When calcular agregados
        SalesAggregationService.SalesAggregates result = salesAggregationService.calculateAggregates(mockSales);

        // Then sucursal correcta identificada
        assertThat(result.getTopBranch()).isEqualTo("San Isidro");
    }

    @Test
    void shouldHandleDateFiltering() {
        // Given ventas con diferentes fechas
        List<Sale> mockSales = List.of(
                createSaleWithDate("OREO_CLASSIC", 10, 1.99, "Miraflores", LocalDateTime.now().minusDays(1)),
                createSaleWithDate("OREO_DOUBLE", 5, 2.49, "San Isidro", LocalDateTime.now().minusDays(3))
        );

        // When calcular agregados
        SalesAggregationService.SalesAggregates result = salesAggregationService.calculateAggregates(mockSales);

        // Then todos los datos incluidos
        assertThat(result.getTotalUnits()).isEqualTo(15);
    }

    @Test
    void shouldCalculateTopSkuWithTies() {
        // Given empate en SKUs
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 10, 2.49, "San Isidro"),
                createSale("OREO_GOLDEN", 5, 3.99, "Miraflores")
        );

        // When calcular agregados
        SalesAggregationService.SalesAggregates result = salesAggregationService.calculateAggregates(mockSales);

        // Then elige el primero en caso de empate (depende del Map implementation)
        assertThat(result.getTopSku()).isIn("OREO_CLASSIC", "OREO_DOUBLE");
    }

    private Sale createSale(String sku, int units, double price, String branch) {
        return Sale.builder()
                .sku(sku)
                .units(units)
                .price(price)
                .branch(branch)
                .soldAt(LocalDateTime.now())
                .build();
    }

    private Sale createSaleWithDate(String sku, int units, double price, String branch, LocalDateTime date) {
        return Sale.builder()
                .sku(sku)
                .units(units)
                .price(price)
                .branch(branch)
                .soldAt(date)
                .build();
    }
}
