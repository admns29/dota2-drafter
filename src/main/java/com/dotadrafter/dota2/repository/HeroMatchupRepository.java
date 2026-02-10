package com.dotadrafter.dota2.repository;

import com.dotadrafter.dota2.model.HeroMatchup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroMatchupRepository extends JpaRepository<HeroMatchup, Long> {
    List<HeroMatchup> findByHeroId(Long heroId);

    List<HeroMatchup> findByMatchupHeroId(Long matchupHeroId);

    List<HeroMatchup> findByPatch(String patch);

    List<HeroMatchup> findByRankTier(HeroMatchup.RankTier rankTier);
}
