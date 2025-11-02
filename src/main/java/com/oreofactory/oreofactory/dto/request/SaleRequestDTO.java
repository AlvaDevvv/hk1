package com.oreofactory.oreofactory.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Units is required")
    @Positive(message = "Units must be positive")
    private Integer units;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Branch is required")
    private String branch;

    private LocalDateTime soldAt;
}
