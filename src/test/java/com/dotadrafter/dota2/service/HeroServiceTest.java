package com.dotadrafter.dota2.service;

import com.dotadrafter.dota2.model.Hero;
import com.dotadrafter.dota2.model.HeroAttribute;
import com.dotadrafter.dota2.repository.HeroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TEST: Unit Test for HeroService
 * 
 * This test demonstrates:
 * - Using Mockito to mock dependencies
 * - Testing a simple service method
 * - Verifying method calls and return values
 * 
 * Key Concepts:
 * - @ExtendWith(MockitoExtension.class): Enables Mockito in JUnit 5
 * - @Mock: Creates a mock object (fake HeroRepository)
 * - @InjectMocks: Creates the service and injects the mocks into it
 * - when().thenReturn(): Defines what the mock should return
 * - verify(): Checks that a method was called on the mock
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Hero Service Unit Tests")
class HeroServiceTest {

    @Mock
    private HeroRepository heroRepository;

    @Mock
    private com.dotadrafter.dota2.client.OpenDotaClient openDotaClient;

    @InjectMocks
    private HeroService heroService;

    private Hero testHero;

    @BeforeEach
    void setUp() {
        // Create a test hero that we'll use in our tests
        testHero = new Hero();
        testHero.setId(1L);
        testHero.setName("Anti-Mage");
        testHero.setPrimaryAttribute(HeroAttribute.AGILITY);
        testHero.setRoles(Arrays.asList("Carry", "Escape"));
        testHero.setBaseStrength(23.0);
        testHero.setBaseAgility(24.0);
        testHero.setBaseIntelligence(12.0);
    }

    @Test
    @DisplayName("Should return all heroes from repository")
    void testGetAllHeroes() {
        // ARRANGE: Set up test data and mock behavior
        List<Hero> expectedHeroes = Arrays.asList(testHero);
        when(heroRepository.findAll()).thenReturn(expectedHeroes);

        // ACT: Call the method we're testing
        List<Hero> actualHeroes = heroService.getAllHeroes();

        // ASSERT: Verify the results
        assertNotNull(actualHeroes, "Hero list should not be null");
        assertEquals(1, actualHeroes.size(), "Should return 1 hero");
        assertEquals("Anti-Mage", actualHeroes.get(0).getName(), "Hero name should match");

        // VERIFY: Check that the repository method was called exactly once
        verify(heroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should save hero to repository")
    void testCreateHero() {
        // ARRANGE
        when(heroRepository.save(testHero)).thenReturn(testHero);

        // ACT
        Hero savedHero = heroService.createHero(testHero);

        // ASSERT
        assertNotNull(savedHero);
        assertEquals("Anti-Mage", savedHero.getName());
        assertEquals(HeroAttribute.AGILITY, savedHero.getPrimaryAttribute());

        // VERIFY
        verify(heroRepository, times(1)).save(testHero);
    }
}
