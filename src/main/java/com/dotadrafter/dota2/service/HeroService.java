package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.model.DraftState;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeroService {

    private final HeroRepository heroRepository;

    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    public Hero createHero(Hero hero) {
        return heroRepository.save(hero);
    }
}
