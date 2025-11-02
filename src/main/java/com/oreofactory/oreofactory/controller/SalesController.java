package com.oreofactory.oreofactory.controller;

import com.oreofactory.oreofactory.dto.request.SaleRequestDTO;
import com.oreofactory.oreofactory.model.entity.Sale;
import com.oreofactory.oreofactory.event.ReportRequestedEvent;
import com.oreofactory.oreofactory.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody SaleRequestDTO request, Authentication authentication) {
        Sale sale = salesService.createSale(request, authentication.getName());
        return ResponseEntity.ok(sale);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable String id, Authentication authentication) {
        Sale sale = salesService.getSale(id, authentication);
        return ResponseEntity.ok(sale);
    }

    @GetMapping
    public ResponseEntity<Page<Sale>> getSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String branch,
            Pageable pageable,
            Authentication authentication) {
        Page<Sale> sales = salesService.getSales(startDate, endDate, branch, pageable, authentication);
        return ResponseEntity.ok(sales);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable String id, @RequestBody SaleRequestDTO request, Authentication authentication) {
        Sale sale = salesService.updateSale(id, request, authentication);
        return ResponseEntity.ok(sale);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable String id, Authentication authentication) {
        salesService.deleteSale(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/summary/weekly")
    public ResponseEntity<Void> generateWeeklySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String branch,
            Authentication authentication) {

        // Publicar evento para procesamiento asíncrono
        eventPublisher.publishEvent(new ReportRequestedEvent(
                this, startDate, endDate, branch, authentication.getName()
        ));

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}