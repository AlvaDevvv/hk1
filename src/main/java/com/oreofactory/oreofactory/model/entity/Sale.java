package com.oreofactory.oreofactory.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false)
    private String sku;

    @NotNull(message = "Units is required")
    @Positive(message = "Units must be positive")
    @Column(nullable = false)
    private Integer units;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @NotBlank(message = "Branch is required")
    @Column(nullable = false)
    private String branch;

    private LocalDateTime soldAt;

    private String createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
