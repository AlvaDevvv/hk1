package com.oreofactory.oreofactory.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaleRequestDTO {
    @NotNull
    @Column(nullable = false)
    private String sku;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    private int units;

    @NotNull
    @Positive
    @Column(nullable = false)
    private double price;

    @NotNull
    @Column(nullable = false)
    private String branch;

    private LocalDateTime soldAt;
}
