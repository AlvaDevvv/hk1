package com.oreofactory.oreofactory.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SummaryRequestDTO {
    private LocalDate from;
    private LocalDate to;
    private String branch;

    @NotNull
    @Email
    private String emailTo;
}
