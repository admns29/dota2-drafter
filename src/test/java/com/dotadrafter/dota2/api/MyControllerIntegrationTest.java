package com.dotadrafter.dota2.api;

import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import com.dotadrafter.dota2.repository.HeroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TEST: Integration Test for MyController
 * 
 * This test demonstrates:
 * - Full Spring Boot application context loading
 * - Testing REST endpoints with MockMvc
 * - Database integration (using H2 in-memory database)
 * - JSON serialization/deserialization
 * - Transaction rollback after each test
 * 
 * Key Concepts:
 * - @SpringBootTest: Loads the full Spring application context
 * - @AutoConfigureMockMvc: Configures MockMvc for testing controllers
 * - @Transactional: Rolls back database changes after each test
 * - MockMvc: Simulates HTTP requests without starting a real server
 * - jsonPath(): Validates JSON response structure and values
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Hero API Integration Tests")
class MyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Hero testHero1;
    private Hero testHero2;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        heroRepository.deleteAll();

        // Create test heroes
        testHero1 = new Hero();
        testHero1.setId(1L);
        testHero1.setName("Anti-Mage");
        testHero1.setPrimaryAttribute(HeroAttribute.AGILITY);
        testHero1.setRoles(Arrays.asList("Carry", "Escape"));
        testHero1.setBaseStrength(23.0);
        testHero1.setBaseAgility(24.0);
        testHero1.setBaseIntelligence(12.0);
        testHero1.setImageUrl("https://example.com/antimage.png");

        testHero2 = new Hero();
        testHero2.setId(2L);
        testHero2.setName("Crystal Maiden");
        testHero2.setPrimaryAttribute(HeroAttribute.INTELLIGENCE);
        testHero2.setRoles(Arrays.asList("Support", "Disabler"));
        testHero2.setBaseStrength(19.0);
        testHero2.setBaseAgility(16.0);
        testHero2.setBaseIntelligence(21.0);
        testHero2.setImageUrl("https://example.com/cm.png");

        // Save to database
        heroRepository.save(testHero1);
        heroRepository.save(testHero2);
    }

    @Test
    @DisplayName("GET /api/heroes should return all heroes")
    void testGetAllHeroes() throws Exception {
        // ACT & ASSERT: Perform GET request and verify response
        mockMvc.perform(get("/api/heroes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Anti-Mage")))
                .andExpect(jsonPath("$[0].primaryAttribute", is("AGILITY")))
                .andExpect(jsonPath("$[0].baseAgility", is(24.0)))
                .andExpect(jsonPath("$[1].name", is("Crystal Maiden")))
                .andExpect(jsonPath("$[1].primaryAttribute", is("INTELLIGENCE")));
    }

    @Test
    @DisplayName("GET /api/heroes should return empty array when no heroes exist")
    void testGetAllHeroesEmpty() throws Exception {
        // ARRANGE: Clear all heroes
        heroRepository.deleteAll();

        // ACT & ASSERT
        mockMvc.perform(get("/api/heroes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/draft/start should create a new draft")
    void testStartDraft() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/draft/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.radiantTurn", is(true)))
                .andExpect(jsonPath("$.pickPhase", is(false)))
                .andExpect(jsonPath("$.complete", is(false)))
                .andExpect(jsonPath("$.radiantPicks", hasSize(0)))
                .andExpect(jsonPath("$.direPicks", hasSize(0)))
                .andExpect(jsonPath("$.radiantBans", hasSize(0)))
                .andExpect(jsonPath("$.direBans", hasSize(0)));
    }

    @Test
    @DisplayName("Should filter heroes by attribute using repository")
    void testFilterHeroesByAttribute() {
        // This demonstrates direct repository testing within integration test
        var agilityHeroes = heroRepository.findByPrimaryAttribute(HeroAttribute.AGILITY);
        var intelligenceHeroes = heroRepository.findByPrimaryAttribute(HeroAttribute.INTELLIGENCE);

        assertEquals(1, agilityHeroes.size());
        assertEquals("Anti-Mage", agilityHeroes.get(0).getName());

        assertEquals(1, intelligenceHeroes.size());
        assertEquals("Crystal Maiden", intelligenceHeroes.get(0).getName());
    }
}
