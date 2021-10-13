package com.github.hteph.utils.enums;

import java.util.HashMap;
import java.util.Map;

public enum LimbType {

    ARM ("arm"),
    LEG ("leg"),
    WING("wing"),
    FIN("fin"),
    SMALL_TAIL("small tail"),
    TAIL("tail"),
    FLUKE("fluke"),
    LONG_TAIL("long tail");

    public final String label;


    private static final Map<String, LimbType> BY_LABEL = new HashMap<>();

    static {
        for (LimbType type: values()) {
            BY_LABEL.put(type.label, type);
        }
    }

    private LimbType(String label) {
        this.label = label;
    }

    public static LimbType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
