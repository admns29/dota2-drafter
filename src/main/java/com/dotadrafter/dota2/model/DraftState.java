package com.dotadrafter.dota2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class DraftState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> radiantPicks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> direPicks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> radiantBans = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> direBans = new ArrayList<>();

    private boolean isRadiantTurn; // true = radiant, false = dire
    private boolean isPickPhase; // true = pick, false = ban

    // Status tracking
    private boolean isComplete = false;
    private int currentTurnIndex = 0; // To track sequence in Captain's Mode
}
