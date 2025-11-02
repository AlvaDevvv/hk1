package com.oreofactory.oreofactory.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class ReportRequestedEvent extends ApplicationEvent {
    private LocalDate startDate;
    private LocalDate endDate;
    private String branch;
    private String email;

    public ReportRequestedEvent(Object source, LocalDate startDate, LocalDate endDate, String branch, String email) {
        super(source);
        this.startDate = startDate;
        this.endDate = endDate;
        this.branch = branch;
        this.email = email;
    }
}