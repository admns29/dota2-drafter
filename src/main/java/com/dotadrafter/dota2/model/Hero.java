package com.dotadrafter.dota2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

// Entity representing a Dota 2 hero
// Stores hero information including name, attributes, roles, and stats
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

    // Unique identifier matching OpenDota's hero ID
    @Id
    private Long id;

    // Display name of the hero (e.g., "npc_dota_hero_axe")
    @Column(unique = true, nullable = false)
    private String name;

    // Primary attribute determining stat growth (STR/AGI/INT/UNIVERSAL)
    @Enumerated(EnumType.STRING)
    private HeroAttribute primaryAttribute;

    // List of roles this hero can play (e.g., Carry, Support, Initiator)
    @ElementCollection
    private List<String> roles;

    // Base stats at level 1 - determines starting attributes
    private double baseStrength;
    private double baseAgility;
    private double baseIntelligence;

    // URL to hero image for UI display
    private String imageUrl;
}
