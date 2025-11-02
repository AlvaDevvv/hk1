package com.oreofactory.oreofactory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AIProperties {
    private String apiKey;
    private String endpoint;
    private String defaultModel;
    private TagProperties tag;

    @Data
    public static class TagProperties {
        private String defaultModel;
        private int limit;
    }
}