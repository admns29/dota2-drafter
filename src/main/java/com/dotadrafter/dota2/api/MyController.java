package com.dotadrafter.dota2.api;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.service.DraftService;
import com.dotadrafter.dota2.service.HeroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend access
public class MyController {

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
        int count = heroService.syncHeroesFromApi();
        return ResponseEntity.ok("Successfully synced " + count + " heroes from OpenDota API");
    }
}
