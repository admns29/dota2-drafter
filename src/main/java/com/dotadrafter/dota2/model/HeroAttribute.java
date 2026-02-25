package com.dotadrafter.dota2.model;

// Represents the primary attribute type for Dota 2 heroes
// Each hero has a primary attribute that determines their stat growth
public enum HeroAttribute {
    // Heroes with high strength gain more HP and HP regeneration
    STRENGTH,
    // Heroes with high agility gain more armor and attack speed
    AGILITY,
    // Heroes with high intelligence gain more mana and mana regeneration
    INTELLIGENCE,
    // New attribute type introduced in Dota 2 - gains all stats equally
    UNIVERSAL
}
