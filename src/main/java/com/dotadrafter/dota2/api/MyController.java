package com.dotadrafter.dota2.api;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.service.DraftService;
import com.dotadrafter.dota2.service.HeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MyController {

    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    private final HeroService heroService;
    private final DraftService draftService;

    public MyController(HeroService heroService, DraftService draftService) {
        this.heroService = heroService;
        this.draftService = draftService;
    }

    // Phase 1: Display all heroes
    @GetMapping("/heroes")
    public List<Hero> getAllHeroes() {
        return heroService.getAllHeroes();
    }

    // Phase 1: Mechanics
    @PostMapping("/draft/start")
    public DraftState startDraft() {
        return draftService.startNewDraft();
    }

    @PostMapping("/draft/{id}/pick/{heroId}")
    public DraftState pickHero(@PathVariable Long id, @PathVariable Long heroId) {
        return draftService.pickHero(id, heroId);
    }

    @PostMapping("/draft/{id}/ban/{heroId}")
    public DraftState banHero(@PathVariable Long id, @PathVariable Long heroId) {
        return draftService.banHero(id, heroId);
    }

    // Phase 2: Sync heroes from OpenDota API
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
}
