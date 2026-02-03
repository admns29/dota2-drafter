package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.client.OpenDotaClient;
import com.dotadrafter.dota2.dto.HeroStatsDto;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeroService {

    private final HeroRepository heroRepository;
    private final OpenDotaClient openDotaClient;

    public HeroService(HeroRepository heroRepository, OpenDotaClient openDotaClient) {
        this.heroRepository = heroRepository;
        this.openDotaClient = openDotaClient;
    }

    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    public Hero createHero(Hero hero) {
        return heroRepository.save(hero);
    }

    public int syncHeroesFromApi() {
        List<HeroStatsDto> heroStats = openDotaClient.fetchHeroStats();

        List<Hero> heroes = heroStats.stream()
                .map(this::mapToHero)
                .toList();

        heroRepository.saveAll(heroes);
        return heroes.size();
    }

    private Hero mapToHero(HeroStatsDto dto) {
        Hero hero = new Hero();
        hero.setId(dto.getId());
        hero.setName(dto.getLocalizedName());
        hero.setPrimaryAttribute(mapAttribute(dto.getPrimaryAttr()));
        hero.setRoles(dto.getRoles());
        hero.setBaseStrength(dto.getBaseStr() != null ? dto.getBaseStr() : 0.0);
        hero.setBaseAgility(dto.getBaseAgi() != null ? dto.getBaseAgi() : 0.0);
        hero.setBaseIntelligence(dto.getBaseInt() != null ? dto.getBaseInt() : 0.0);
        hero.setImageUrl("https://cdn.cloudflare.steamstatic.com" + dto.getImg());
        return hero;
    }

    private HeroAttribute mapAttribute(String attr) {
        return switch (attr != null ? attr.toLowerCase() : "") {
            case "str" -> HeroAttribute.STRENGTH;
            case "agi" -> HeroAttribute.AGILITY;
            case "int" -> HeroAttribute.INTELLIGENCE;
            case "all" -> HeroAttribute.UNIVERSAL;
            default -> HeroAttribute.STRENGTH;
        };
    }
}
