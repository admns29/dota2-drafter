package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.repository.DraftRepository;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

@Service
public class DraftService {

    private final DraftRepository draftRepository;
    private final HeroRepository heroRepository;

    public DraftService(DraftRepository draftRepository, HeroRepository heroRepository) {
        this.draftRepository = draftRepository;
        this.heroRepository = heroRepository;
    }

    public DraftState startNewDraft() {
        DraftState draft = new DraftState();
        draft.setRadiantTurn(true);
        draft.setPickPhase(false);
        return draftRepository.save(draft);
    }

    public DraftState pickHero(Long draftId, Long heroId) {
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

        // Add to appropriate team
        if (draft.isRadiantTurn()) {
            draft.getRadiantPicks().add(hero);
        } else {
            draft.getDirePicks().add(hero);
        }

        advanceTurn(draft);
        return draftRepository.save(draft);
    }

    public DraftState banHero(Long draftId, Long heroId) {
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

        // Add to appropriate team's bans
        if (draft.isRadiantTurn()) {
            draft.getRadiantBans().add(hero);
        } else {
            draft.getDireBans().add(hero);
        }

        advanceTurn(draft);
        return draftRepository.save(draft);
    }

    private boolean isHeroInDraft(DraftState draft, Long heroId) {
        return draft.getRadiantPicks().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getDirePicks().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getRadiantBans().stream().anyMatch(h -> h.getId().equals(heroId))
                || draft.getDireBans().stream().anyMatch(h -> h.getId().equals(heroId));
    }

    private void advanceTurn(DraftState draft) {
        int turnIndex = draft.getCurrentTurnIndex();
        turnIndex++;
        draft.setCurrentTurnIndex(turnIndex);

        // Dota 2 Captain's Mode draft order:
        // Ban phase 1: 1-1-1-1-1-1 (6 bans)
        // Pick phase 1: 1-2-2-1 (6 picks)
        // Ban phase 2: 1-1-1-1 (4 bans)
        // Pick phase 2: 2-2-1 (5 picks total, one more pick)
        // Final pick: 1 (total 10 picks, 10 bans)

        // Simplified version: alternate turns, switch to pick after 4 bans
        if (turnIndex >= 4 && !draft.isPickPhase()) {
            draft.setPickPhase(true);
        }

        // Check if draft is complete (5 picks + 2 bans per team)
        int totalPicks = draft.getRadiantPicks().size() + draft.getDirePicks().size();
        int totalBans = draft.getRadiantBans().size() + draft.getDireBans().size();

        if (totalPicks >= 10 || (totalPicks >= 6 && totalBans >= 8)) {
            draft.setComplete(true);
        }

        // Toggle turn
        draft.setRadiantTurn(!draft.isRadiantTurn());
    }
}
