package com.github.hteph.utils.enums;


public enum HydrosphereDescription {
    NONE("None"),
    CRUSTAL("Crustal"),
    VAPOR("Vapor transient"),
    LIQUID("Liquid"),
    ICE_SHEET("Ice sheet"),
    REMNANTS("Remnant"),
    BOILING("Boiling seas");

    public final String label;

    HydrosphereDescription(String label) {
        this.label = label;
    }

}
