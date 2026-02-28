package com.dotadrafter.dota2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entity representing the state of a hero draft (Captain's Mode)
// Tracks picks, bans, current turn, and draft completion status
@Entity
@Data
@NoArgsConstructor
public class DraftState {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Timestamp when the draft was started
    private LocalDateTime startTime = LocalDateTime.now();

    // Heroes picked by Radiant team
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> radiantPicks = new ArrayList<>();

    // Heroes picked by Dire team
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> direPicks = new ArrayList<>();

    // Heroes banned by Radiant team
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> radiantBans = new ArrayList<>();

    // Heroes banned by Dire team
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hero> direBans = new ArrayList<>();

    // Flag indicating whose turn it is: true = Radiant, false = Dire
    private boolean isRadiantTurn;
    // Flag indicating current phase: true = pick phase, false = ban phase
    private boolean isPickPhase;

    // Flag indicating if draft has been completed
    private boolean isComplete = false;
    // Counter tracking the turn sequence in Captain's Mode draft order
    private int currentTurnIndex = 0;
}
