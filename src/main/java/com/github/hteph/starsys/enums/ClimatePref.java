package com.github.hteph.starsys.enums;

/**
 */
public enum ClimatePref implements baseEnum{

    COLD ("Cold", "Climates with low evaporation and long periods of frozen conditions."),
    WARM ("Warm", "A climate where the average temperature allows for a number of different water and temperature regimes, but not peristently extreme."),
    HOT ("Hot", "A climate of high evaporation (not necessarily dry) and never freezing conditions.");

    private final String name;
    private final String description;

    ClimatePref(String name, String description) {
        this.name =name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
