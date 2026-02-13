package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.client.OpenDotaClient;
import com.dotadrafter.dota2.dto.HeroStatsDto;
import com.dotadrafter.dota2.dto.MatchupDto;
import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import com.dotadrafter.dota2.model.HeroMatchup;
import com.dotadrafter.dota2.repository.HeroMatchupRepository;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class HeroService {

    private final HeroRepository heroRepository;
    private final HeroMatchupRepository heroMatchupRepository;
    private final OpenDotaClient openDotaClient;

    public HeroService(HeroRepository heroRepository, HeroMatchupRepository heroMatchupRepository,
            OpenDotaClient openDotaClient) {
        this.heroRepository = heroRepository;
        this.heroMatchupRepository = heroMatchupRepository;
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

    public List<HeroMatchup> getAllMatchups(Long heroId) {
        return heroMatchupRepository.findByHeroId(heroId);
    }

    // Rank tier values for OpenDota API: Legend = 50-55, Ancient = 60-65
    private static final int LEGEND_RANK_TIER = 50;
    private static final int ANCIENT_RANK_TIER = 60;
    private static final Duration API_CALL_DELAY = Duration.ofMillis(1100); // 1.1 seconds to respect rate limits

    public int syncHeroMatchupsFromApi(Long heroId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new RuntimeException("Hero not found: " + heroId));

        List<HeroMatchup> allMatchups = new ArrayList<>();

        // Fetch Legend rank matchups
        List<MatchupDto> legendMatchups = openDotaClient.fetchMatchups(heroId, LEGEND_RANK_TIER);
        allMatchups.addAll(
            legendMatchups.stream()
                .filter(dto -> heroRepository.existsById(dto.getOpponentHeroId()))
                .map(dto -> mapToHeroMatchup(hero, dto, HeroMatchup.RankTier.LEGEND))
                .toList()
        );

        // Wait 1.1 seconds to respect API rate limits
        sleepSafely(API_CALL_DELAY);

        // Fetch Ancient rank matchups
        List<MatchupDto> ancientMatchups = openDotaClient.fetchMatchups(heroId, ANCIENT_RANK_TIER);
        allMatchups.addAll(
            ancientMatchups.stream()
                .filter(dto -> heroRepository.existsById(dto.getOpponentHeroId()))
                .map(dto -> mapToHeroMatchup(hero, dto, HeroMatchup.RankTier.ANCIENT))
                .toList()
        );

        if (!allMatchups.isEmpty()) {
            // Clear existing matchups for this hero to avoid duplicates
            List<HeroMatchup> existing = heroMatchupRepository.findByHeroId(heroId);
            heroMatchupRepository.deleteAll(existing);

            heroMatchupRepository.saveAll(allMatchups);
        }

        return allMatchups.size();
    }

    private void sleepSafely(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during API rate limiting delay", e);
        }
    }

    private HeroMatchup mapToHeroMatchup(Hero hero, MatchupDto dto, HeroMatchup.RankTier rankTier) {
        Hero opponent = heroRepository.findById(dto.getOpponentHeroId()).orElse(null);

        HeroMatchup heroMatchup = new HeroMatchup();
        heroMatchup.setHero(hero);
        heroMatchup.setMatchupHero(opponent);

        // calculate simple win rate: (wins / games) * 100
        double winRate = dto.getGamesPlayed() > 0 ? ((double) dto.getWins() / dto.getGamesPlayed()) * 100 : 0;
        heroMatchup.setAdvantage(winRate - 50.0); // Simple advantage metric

        heroMatchup.setGamesPlayed(dto.getGamesPlayed());
        heroMatchup.setPatch("ALL_TIME"); // OpenDota endpoint returns lifetime stats by default unless filtered
        heroMatchup.setRankTier(rankTier); // Use the specified rank tier

        return heroMatchup;
    }

}
