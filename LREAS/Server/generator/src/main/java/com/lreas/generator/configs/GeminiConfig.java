package com.lreas.generator.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
@Getter @Setter
public class GeminiConfig {
    private String modelCode;
    private String baseUrl;
    private String cacheUrl;
    private String apiKey;
}
