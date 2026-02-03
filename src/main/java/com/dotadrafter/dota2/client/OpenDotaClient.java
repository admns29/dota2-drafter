package com.dotadrafter.dota2.client;

import com.dotadrafter.dota2.dto.HeroStatsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class OpenDotaClient {

    private final WebClient webClient;

    public OpenDotaClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.opendota.com")
                .build();
    }

    public List<HeroStatsDto> fetchHeroStats() {
        return webClient.get()
                .uri("/api/heroStats")
                .retrieve()
                .bodyToMono(HeroStatsDto[].class)
                .map(List::of)
                .block(); // Blocking call for simplicity
    }
}
