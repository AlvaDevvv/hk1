package com.oreofactory.oreofactory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumReportRequestDTO {
    private LocalDate from;
    private LocalDate to;
    private String branch;
    private String emailTo;
    private String format;
    private boolean includeCharts;
    private boolean attachPdf;
}