package com.github.hteph.utils.enums;

public enum SensorOrgan {

    ALL_IN_LIMBS ("Limb sensor organs", "All of the cretures organs is located in limbs."),
    SECONDARY_IN_LIMBS ("Secondary sensor organs in limbs", "Some of the creatures secondary sensor organs is located in limbs."),
    ALL_IN_BODY_SEGMENTS ("All major sensor organs in body", "All of the creatures organs is located in body.");

    private final String name;
    private final String description;

    SensorOrgan(String name, String description) {
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
