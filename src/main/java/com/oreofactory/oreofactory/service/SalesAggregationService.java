package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.model.entity.Sale;
import com.oreofactory.oreofactory.repository.SaleRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesAggregationService {

    private final SaleRepository saleRepository;

    // ✅ AGREGAR ESTE MÉTODO
    public SalesAggregates getAggregates(LocalDate startDate, LocalDate endDate, String branch) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Sale> sales;
        if (branch != null && !branch.trim().isEmpty()) {
            sales = saleRepository.findByBranchAndSoldAtBetween(branch, start, end);
        } else {
            sales = saleRepository.findBySoldAtBetween(start, end);
        }

        return calculateAggregates(sales);
    }


    public SalesAggregates calculateAggregates(List<Sale> sales) {
        if (sales.isEmpty()) {
            return new SalesAggregates(0, 0.0, "N/A", "N/A");
        }

        int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();
        double totalRevenue = sales.stream().mapToDouble(s -> s.getUnits() * s.getPrice()).sum();

        // Top SKU
        Map<String, Integer> skuUnits = sales.stream()
                .collect(Collectors.groupingBy(Sale::getSku, Collectors.summingInt(Sale::getUnits)));
        String topSku = skuUnits.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Top Branch
        Map<String, Integer> branchUnits = sales.stream()
                .collect(Collectors.groupingBy(Sale::getBranch, Collectors.summingInt(Sale::getUnits)));
        String topBranch = branchUnits.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch);
    }

    @Data
    public static class SalesAggregates {
        private final int totalUnits;
        private final double totalRevenue;
        private final String topSku;
        private final String topBranch;

        public SalesAggregates(int totalUnits, double totalRevenue, String topSku, String topBranch) {
            this.totalUnits = totalUnits;
            this.totalRevenue = totalRevenue;
            this.topSku = topSku;
            this.topBranch = topBranch;
        }
    }
}
