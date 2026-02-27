package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.client.OpenDotaClient;
import com.dotadrafter.dota2.dto.HeroStatsDto;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Service layer for hero-related business logic
// Handles hero data retrieval and synchronization from external API
@Service
public class HeroService {

    private final HeroRepository heroRepository;
    private final OpenDotaClient openDotaClient;

    public HeroService(HeroRepository heroRepository, OpenDotaClient openDotaClient) {
        this.heroRepository = heroRepository;
        this.openDotaClient = openDotaClient;
    }

    // Retrieves all heroes from the database
    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    // Creates a new hero in the database
    public Hero createHero(Hero hero) {
        return heroRepository.save(hero);
    }

    // Synchronizes hero data from OpenDota API
    // Fetches all heroes from external API and saves to local database
    // Returns the number of heroes synced
    public int syncHeroesFromApi() {
        // Fetch hero statistics from OpenDota API
        List<HeroStatsDto> heroStats = openDotaClient.fetchHeroStats();

        // Map API response to internal Hero entities
        List<Hero> heroes = heroStats.stream()
                .map(this::mapToHero)
                .toList();

        // Save all heroes to database and return count
        heroRepository.saveAll(heroes);
        return heroes.size();
    }

    // Maps API response DTO to internal Hero entity
    private Hero mapToHero(HeroStatsDto dto) {
        Hero hero = new Hero();
        hero.setId(dto.getId());
        hero.setName(dto.getLocalizedName());
        hero.setPrimaryAttribute(mapAttribute(dto.getPrimaryAttr()));
        hero.setRoles(dto.getRoles());
        // Handle null values by defaulting to 0
        hero.setBaseStrength(dto.getBaseStr() != null ? dto.getBaseStr() : 0.0);
        hero.setBaseAgility(dto.getBaseAgi() != null ? dto.getBaseAgi() : 0.0);
        hero.setBaseIntelligence(dto.getBaseInt() != null ? dto.getBaseInt() : 0.0);
        // Construct full image URL from Steam CDN base path
        hero.setImageUrl("https://cdn.cloudflare.steamstatic.com" + dto.getImg());
        return hero;
    }

    // Converts API attribute string to internal enum
    private HeroAttribute mapAttribute(String attr) {
        return switch (attr != null ? attr.toLowerCase() : "") {
            case "str" -> HeroAttribute.STRENGTH;
            case "agi" -> HeroAttribute.AGILITY;
            case "int" -> HeroAttribute.INTELLIGENCE;
            case "all" -> HeroAttribute.UNIVERSAL;
            // Default to STRENGTH if unknown attribute
            default -> HeroAttribute.STRENGTH;
        };
    }
}
