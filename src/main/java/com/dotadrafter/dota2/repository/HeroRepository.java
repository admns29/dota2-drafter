package com.dotadrafter.dota2.repository;

import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository interface for Hero entity
// Provides database operations for hero data using Spring Data JPA
@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    // Find a hero by exact name match
    Optional<Hero> findByName(String name);
    // Find all heroes with a specific primary attribute
    List<Hero> findByPrimaryAttribute(HeroAttribute attribute);
}
