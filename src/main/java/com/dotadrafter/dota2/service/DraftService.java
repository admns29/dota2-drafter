package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.repository.DraftRepository;
import org.springframework.stereotype.Service;

@Service
public class DraftService {

    private final DraftRepository draftRepository;

    public DraftService(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    public DraftState startNewDraft() {
        DraftState draft = new DraftState();
        draft.setRadiantTurn(true);
        draft.setPickPhase(false);
        return draftRepository.save(draft);
    }

    // Skeleton method for picking
    public DraftState pickHero(Long draftId, Long heroId) {
        // Logic to validate turn, add to radiant/dire picks, advance turn
        // Fetch draft, update, save
        return draftRepository.findById(draftId).orElseThrow();
    }

    // Skeleton method for banning
    public DraftState banHero(Long draftId, Long heroId) {
        // Logic to validate turn, add to bans, advance turn
        return draftRepository.findById(draftId).orElseThrow();
    }
}
