package com.lreas.quiz.utils;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter @Setter
public class MinioClientConfig {
    private String url;
    private String bucket;
    private String accessKey;
    private String secretKey;
}