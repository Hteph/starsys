package com.github.hteph.utils.enums;

public enum Breathing {

    NONE ("No life"),
    OXYGEN ("Oxygen breathing"),
    AMMONIA ("Ammonia breathers"),
    CHLORIDE ("Chloride breathers");

    public final String label;

    private Breathing(String label) {
        this.label = label;
    }
}
