package com.github.hteph.starsys.enums;

/**
 */
public enum TrophicLevels implements baseEnum{

CARNIVORE("Carnivore","Predator, member of the top trophic level"),
OMNIVORE("Omnivore","Displays feeding preferences from both Herbivore and Carnivore"),
AUTOTROPH("Ergivore","Primary Producer"),
HERBIVORE("Herbivore","Predation of autotrophes and similar non resisting primary producers");

    private final String name;
    private final String description;

    TrophicLevels(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

}
