package com.github.hteph.starsys.enums;

public enum StellarObjectType {

    STAR("S"),
    JOVIAN("J"),
    TERRESTRIAL("T"),
    ASTEROID_BELT("A"),
    MOON("M"),
    OTHER("O");

    private final String label;

    private StellarObjectType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
