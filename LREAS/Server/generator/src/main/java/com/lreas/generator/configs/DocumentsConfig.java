package com.lreas.generator.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "documents")
@Getter @Setter
public class DocumentsConfig {
    private String baseUrl;
}
