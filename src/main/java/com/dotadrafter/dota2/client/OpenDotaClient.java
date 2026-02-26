package com.dotadrafter.dota2.client;

import com.dotadrafter.dota2.dto.HeroStatsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

// HTTP client for communicating with OpenDota API
// Provides methods to fetch hero data from external service
@Component
public class OpenDotaClient {

    // WebClient for making async HTTP requests to external API
    private final WebClient webClient;

    // Initialize WebClient with OpenDota API base URL
    public OpenDotaClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.opendota.com")
                .build();
    }

    // Fetches hero statistics from OpenDota API
    // Calls /api/heroStats endpoint and returns list of all heroes
    public List<HeroStatsDto> fetchHeroStats() {
        // Make GET request to heroStats endpoint
        HeroStatsDto[] heroArray = webClient.get()
                .uri("/api/heroStats")
                .retrieve()
                .bodyToMono(HeroStatsDto[].class)
                .block();  // Blocking call to wait for response

        // Convert array to list, handling null response
        return heroArray != null ? Arrays.asList(heroArray) : List.of();
    }
}
