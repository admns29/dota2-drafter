package com.dotadrafter.dota2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "hero_matchups",
                // This ensures one entry per specific scenario.
                uniqueConstraints = {
                                @UniqueConstraint(columnNames = { "hero_id", "matchup_hero_id", "patch", "rank_tier" })
                },
                // Indexes make searching by patch and rank instant
                indexes = {
                                @Index(name = "idx_patch_rank", columnList = "patch, rank_tier")
                })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeroMatchup {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "hero_id", nullable = false)
        private Hero hero;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "matchup_hero_id", nullable = false)
        private Hero matchupHero;

        // Advantage percentage (e.g., 4.5 for +4.5% winrate)
        private double advantage;

        // The sample size (how many games this stat is based on)
        private long gamesPlayed;

        // e.g., "7.35", "7.36"
        @Column(length = 10, nullable = false)
        private String patch;

        @Enumerated(EnumType.STRING)
        @Column(name = "rank_tier", length = 20, nullable = false)
        private RankTier rankTier;

        // Enum for strict type safety
        public enum RankTier {
                LEGEND,
                ANCIENT,
                IMMORTAL
        }
}