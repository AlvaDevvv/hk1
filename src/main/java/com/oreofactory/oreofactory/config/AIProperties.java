package com.oreofactory.oreofactory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.chat")
public class AIProperties {
    private String endpoint;
    private String apiKey;
    private String defaultModel;

    @Data
    @Component
    @ConfigurationProperties(prefix = "ai.tag")
    public static class TagProperties {
        private String defaultModel;
        private int limit;
    }
}