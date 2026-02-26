package com.dotadrafter.dota2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

// Data Transfer Object for hero statistics from OpenDota API
// Maps JSON response from /api/heroStats endpoint
@Data
public class HeroStatsDto {

    // OpenDota's unique identifier for the hero
    private Long id;

    // Human-readable hero name (e.g., "Axe", "Bloodseeker")
    @JsonProperty("localized_name")
    private String localizedName;

    // Primary attribute code: "str", "agi", "int", or "all" (universal)
    @JsonProperty("primary_attr")
    private String primaryAttr;

    // Attack type: "Melee" or "Ranged"
    @JsonProperty("attack_type")
    private String attackType;

    // List of roles the hero can fulfill
    private List<String> roles;

    // Base strength at level 1
    @JsonProperty("base_str")
    private Double baseStr;

    // Base agility at level 1
    @JsonProperty("base_agi")
    private Double baseAgi;

    // Base intelligence at level 1
    @JsonProperty("base_int")
    private Double baseInt;

    // Relative path to hero image on Steam CDN
    private String img;
}
