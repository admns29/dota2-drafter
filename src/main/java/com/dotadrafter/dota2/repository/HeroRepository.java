package com.dotadrafter.dota2.repository;

import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    Optional<Hero> findByName(String name);
    List<Hero> findByPrimaryAttribute(HeroAttribute attribute);
}
