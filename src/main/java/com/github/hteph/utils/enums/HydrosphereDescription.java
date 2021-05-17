package com.github.hteph.utils.enums;


public enum HydrosphereDescription {
    NONE("None"),
    CRUSTAL ("Crustal"),
    VAPOR("Vapor trace"),
    LIQUID("Liquid"),
    ICE_SHEET("Ice sheet"),
    REMNANTS("Remnant");

    public final String label;

    private HydrosphereDescription(String label) {
        this.label = label;
    }

}
