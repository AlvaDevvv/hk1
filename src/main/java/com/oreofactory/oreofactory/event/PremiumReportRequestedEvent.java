package com.oreofactory.oreofactory.event;

import com.oreofactory.oreofactory.dto.request.PremiumReportRequestDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PremiumReportRequestedEvent extends ApplicationEvent {
    private final PremiumReportRequestDTO request;
    private final String username;

    public PremiumReportRequestedEvent(Object source, PremiumReportRequestDTO request, String username) {
        super(source);
        this.request = request;
        this.username = username;
    }
}
