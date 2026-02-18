package com.dotadrafter.dota2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MatchupDto {
    @JsonProperty("hero_id")
    private Long opponentHeroId;

    @JsonProperty("games_played")
    private long gamesPlayed;

    @JsonProperty("wins")
    private long wins;
}
