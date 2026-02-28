package com.dotadrafter.dota2.api;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroMatchup;
import com.dotadrafter.dota2.service.DraftService;
import com.dotadrafter.dota2.service.HeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller for Dota 2 draft application
// Provides endpoints for hero data and draft management
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Allow cross-origin requests for frontend
public class MyController {

    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    private final HeroService heroService;
    private final DraftService draftService;

    public MyController(HeroService heroService, DraftService draftService) {
        this.heroService = heroService;
        this.draftService = draftService;
    }

    // GET /api/heroes - Returns all available heroes
    // Used for Phase 1: displaying hero selection UI
    @GetMapping("/heroes")
    public List<Hero> getAllHeroes() {
        return heroService.getAllHeroes();
    }

    // POST /api/draft/start - Starts a new draft session
    // Initializes draft state with Radiant team going first
    @PostMapping("/draft/start")
    public DraftState startDraft() {
        return draftService.startNewDraft();
    }

    // POST /api/draft/{id}/pick/{heroId} - Picks a hero for current team
    // Adds hero to the team whose turn it is during pick phase
    @PostMapping("/draft/{id}/pick/{heroId}")
    public DraftState pickHero(@PathVariable Long id, @PathVariable Long heroId) {
        return draftService.pickHero(id, heroId);
    }

    // POST /api/draft/{id}/ban/{heroId} - Bans a hero for current team
    // Removes hero from availability during ban phase
    @PostMapping("/draft/{id}/ban/{heroId}")
    public DraftState banHero(@PathVariable Long id, @PathVariable Long heroId) {
        return draftService.banHero(id, heroId);
    }

    // POST /api/heroes/sync - Synchronizes heroes from OpenDota API
    // Fetches latest hero data from external API and saves to database
    @PostMapping("/heroes/sync")
    public ResponseEntity<String> syncHeroes() {
        try {
            log.info("Starting hero sync from OpenDota API...");
            int count = heroService.syncHeroesFromApi();
            log.info("Successfully synced {} heroes", count);
            return ResponseEntity.ok("Successfully synced " + count + " heroes from OpenDota API");
        } catch (Exception e) {
            log.error("Failed to sync heroes from API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error syncing heroes: " + e.getMessage());
        }
    }

    // Phase 2: Sync matchups from OpenDota API
    @PostMapping("/heroes/{id}/matchups/sync")
    public ResponseEntity<String> syncMatchups(@PathVariable Long id) {
        try {
            log.info("Starting matchup sync for hero {} from OpenDota API...", id);
            int count = heroService.syncHeroMatchupsFromApi(id);
            log.info("Successfully synced {} matchups for hero {}", count, id);
            return ResponseEntity.ok("Successfully synced " + count + " matchups from OpenDota API");
        } catch (Exception e) {
            log.error("Failed to sync matchups from API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error syncing matchups: " + e.getMessage());
        }
    }

    // Phase 2: Display all matchups
    @GetMapping("/heroes/{id}/matchups")
    public List<HeroMatchup> getAllMatchups(@PathVariable Long id) {
        return heroService.getAllMatchups(id);
    }
}
