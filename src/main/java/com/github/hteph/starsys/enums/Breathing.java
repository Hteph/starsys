package com.github.hteph.starsys.enums;

public enum Breathing {

    NONE ("No life"),
    OXYGEN ("Oxygen breathing"),
    AMMONIA ("Ammonia breathers"),
    CHLORIDE ("Chloride breathers"),
    PROTO ("Developing respiration");

    public final String label;

    private Breathing(String label) {
        this.label = label;
    }
}
