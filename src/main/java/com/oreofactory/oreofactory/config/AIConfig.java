package com.oreofactory.oreofactory.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AIProperties.class, AIProperties.TagProperties.class})
@RequiredArgsConstructor
public class AIConfig {
    // La configuración se carga automáticamente mediante @EnableConfigurationProperties
}