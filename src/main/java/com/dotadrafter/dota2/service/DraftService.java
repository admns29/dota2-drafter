package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.repository.DraftRepository;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

// Service layer for draft (pick/ban) logic
// Implements Captain's Mode draft mechanics for Dota 2 hero selection
@Service
public class DraftService {

    private final DraftRepository draftRepository;
    private final HeroRepository heroRepository;

    public DraftService(DraftRepository draftRepository, HeroRepository heroRepository) {
        this.draftRepository = draftRepository;
        this.heroRepository = heroRepository;
    }

    // Creates a new draft session
    // Initializes with Radiant team going first, starting in ban phase
    public DraftState startNewDraft() {
        DraftState draft = new DraftState();
        draft.setRadiantTurn(true);  // Radiant team has first turn
        draft.setPickPhase(false);   // Start with ban phase
        return draftRepository.save(draft);
    }

    // Handles hero pick action during pick phase
    // Adds hero to appropriate team's picks based on current turn
    public DraftState pickHero(Long draftId, Long heroId) {
        // Fetch draft and hero from database
        DraftState draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new RuntimeException("Hero not found"));

        // Validate it's pick phase
        if (!draft.isPickPhase()) {
            throw new RuntimeException("Not in pick phase");
        }

        // Check if hero already picked or banned
        if (isHeroInDraft(draft, heroId)) {
            throw new RuntimeException("Hero already picked or banned");
        }

        // Add to appropriate team based on whose turn it is
        if (draft.isRadiantTurn()) {
            draft.getRadiantPicks().add(hero);
        } else {
            draft.getDirePicks().add(hero);
        }

        // Advance to next turn and save
        advanceTurn(draft);
        return draftRepository.save(draft);
    }

    // Handles hero ban action during ban phase
    // Adds hero to appropriate team's bans based on current turn
    public DraftState banHero(Long draftId, Long heroId) {
        // Fetch draft and hero from database
        DraftState draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new RuntimeException("Hero not found"));

        // Validate it's ban phase
        if (draft.isPickPhase()) {
            throw new RuntimeException("Not in ban phase");
        }

        // Check if hero already picked or banned
        if (isHeroInDraft(draft, heroId)) {
            throw new RuntimeException("Hero already picked or banned");
        }

        // Add to appropriate team's bans based on current turn
        if (draft.isRadiantTurn()) {
            draft.getRadiantBans().add(hero);
        } else {
            draft.getDireBans().add(hero);
        }

        // Advance to next turn and save
        advanceTurn(draft);
        return draftRepository.save(draft);
    }

    // Checks if a hero has already been picked or banned in this draft
    private boolean isHeroInDraft(DraftState draft, Long heroId) {
        return draft.getRadiantPicks().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getDirePicks().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getRadiantBans().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getDireBans().stream().anyMatch(h -> h.getId().equals(heroId));
    }

    // Advances the draft to the next turn
    // Handles phase transitions and draft completion logic
    private void advanceTurn(DraftState draft) {
        // Increment turn counter
        int turnIndex = draft.getCurrentTurnIndex();
        turnIndex++;
        draft.setCurrentTurnIndex(turnIndex);

        // Dota 2 Captain's Mode draft order:
        // Ban phase 1: 1-1-1-1-1-1 (6 bans total, 3 per team)
        // Pick phase 1: 1-2-2-1 (6 picks total)
        // Ban phase 2: 1-1-1-1 (4 bans total)
        // Pick phase 2: 2-2-1 (5 picks total, one extra for first pick)
        // Total: 10 picks and 10 bans

        // Simplified version: alternate turns, switch to pick after 4 bans
        // After turn 4, transition from ban phase to pick phase
        if (turnIndex >= 4 && !draft.isPickPhase()) {
            draft.setPickPhase(true);
        }

        // Check if draft is complete
        // Complete when: 10 picks made OR (6 picks + 8 bans made)
        int totalPicks = draft.getRadiantPicks().size() + draft.getDirePicks().size();
        int totalBans = draft.getRadiantBans().size() + draft.getDireBans().size();

        if (totalPicks >= 10 || (totalPicks >= 6 && totalBans >= 8)) {
            draft.setComplete(true);
        }

        // Toggle turn to alternate between Radiant and Dire teams
        draft.setRadiantTurn(!draft.isRadiantTurn());
    }
}
