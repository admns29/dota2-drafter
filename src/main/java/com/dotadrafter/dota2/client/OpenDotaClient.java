package com.dotadrafter.dota2.client;

import com.dotadrafter.dota2.dto.HeroStatsDto;
import com.dotadrafter.dota2.dto.MatchupDto;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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
        HeroStatsDto[] heroArray = webClient.get()
                .uri("/api/heroStats")
                .retrieve()
                .bodyToMono(HeroStatsDto[].class)
                .block();

        return heroArray != null ? Arrays.asList(heroArray) : List.of();
    }

    public List<MatchupDto> fetchMatchups(String patch, String rankTier) {
        MatchupDto[] matchupArray = webClient.get()
                .uri("/api/heroes/matchups?patch=" + patch + "&rank_tier=" + rankTier)
                .retrieve()
                .bodyToMono(MatchupDto[].class)
                .block();

        return matchupArray != null ? Arrays.asList(matchupArray) : List.of();
    }
}
