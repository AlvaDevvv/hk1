package com.oreofactory.oreofactory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumReportResponseDTO {
    private String requestId;
    private String status;
    private String message;
    private String estimatedTime;
    private List<String> features;
}
