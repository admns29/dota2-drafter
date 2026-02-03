package com.dotadrafter.dota2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private HeroAttribute primaryAttribute;

    @ElementCollection
    private List<String> roles; // Carry, Support, etc.

    // Phase 2: Base Stats
    private double baseStrength;
    private double baseAgility;
    private double baseIntelligence;
    
    private String imageUrl; // For UI display
}
