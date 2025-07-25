package com.zvonbot.interview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ZvonobotConfig {
    
    @Value("${zvonobot.api.base-url}")
    private String baseUrl;
    
    @Value("${zvonobot.api.key}")
    private String apiKey;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                .build();
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
} 