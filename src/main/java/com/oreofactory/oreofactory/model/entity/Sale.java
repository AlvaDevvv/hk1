package com.oreofactory.oreofactory.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank
    @Column(nullable = false)
    private String sku;

    @NotBlank
    @Column(nullable = false)
    private Integer units;

    @NotBlank
    @Column(nullable = false)
    private Double price;

    @NotBlank
    @Column(nullable = false)
    private String branch;

    private LocalDateTime soldAt;

    private String createdBy;

    private LocalDateTime createdAt;

}
