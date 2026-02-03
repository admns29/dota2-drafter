package com.dotadrafter.dota2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HeroStatsDto {

    private Long id;

    @JsonProperty("localized_name")
    private String localizedName;

    @JsonProperty("primary_attr")
    private String primaryAttr;

    @JsonProperty("attack_type")
    private String attackType;

    private List<String> roles;

    @JsonProperty("base_str")
    private Double baseStr;

    @JsonProperty("base_agi")
    private Double baseAgi;

    @JsonProperty("base_int")
    private Double baseInt;

    private String img;
}
