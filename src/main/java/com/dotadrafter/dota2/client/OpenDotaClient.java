package com.dotadrafter.dota2.client;

import com.dotadrafter.dota2.dto.HeroStatsDto;
import com.dotadrafter.dota2.dto.MatchupDto;
import com.dotadrafter.dota2.exception.OpenDotaApiException;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    public List<MatchupDto> fetchMatchups(Long heroId) {
        return fetchMatchups(heroId, null);
    }

    public List<MatchupDto> fetchMatchups(Long heroId, Integer rankTier) {
        String uri = "/api/heroes/{heroId}/matchups";
        
        try {
            MatchupDto[] matchupArray;
            if (rankTier != null) {
                matchupArray = webClient.get()
                        .uri(uri + "?rank_tier={rankTier}", heroId, rankTier)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> 
                            Mono.error(new OpenDotaApiException(
                                "Client error calling OpenDota API for hero " + heroId + 
                                " with rank tier " + rankTier + ": " + response.statusCode())))
                        .onStatus(HttpStatusCode::is5xxServerError, response -> 
                            Mono.error(new OpenDotaApiException(
                                "Server error from OpenDota API for hero " + heroId + 
                                " with rank tier " + rankTier + ": " + response.statusCode())))
                        .bodyToMono(MatchupDto[].class)
                        .block(Duration.ofSeconds(30));
            } else {
                matchupArray = webClient.get()
                        .uri(uri, heroId)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> 
                            Mono.error(new OpenDotaApiException(
                                "Client error calling OpenDota API for hero " + heroId + 
                                ": " + response.statusCode())))
                        .onStatus(HttpStatusCode::is5xxServerError, response -> 
                            Mono.error(new OpenDotaApiException(
                                "Server error from OpenDota API for hero " + heroId + 
                                ": " + response.statusCode())))
                        .bodyToMono(MatchupDto[].class)
                        .block(Duration.ofSeconds(30));
            }

            return matchupArray != null ? Arrays.asList(matchupArray) : List.of();
        } catch (WebClientResponseException e) {
            throw new OpenDotaApiException("HTTP error from OpenDota API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new OpenDotaApiException("Failed to fetch matchups from OpenDota API for hero " + heroId, e);
        }
    }
}
