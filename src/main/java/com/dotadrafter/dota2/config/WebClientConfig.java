package com.dotadrafter.dota2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Spring configuration class for WebClient bean setup
// Provides reactive HTTP client for external API calls
@Configuration
public class WebClientConfig {

    // Creates a WebClient.Builder bean for making HTTP requests
    // Used by OpenDotaClient to communicate with OpenDota API
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
