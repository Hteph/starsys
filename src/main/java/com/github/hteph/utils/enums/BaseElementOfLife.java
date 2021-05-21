package com.github.hteph.utils.enums;

public enum BaseElementOfLife {

    CARBON ("Carbon based"),
    Silica ("Silica based");

    public final String label;

    private BaseElementOfLife(String label) {
        this.label = label;
    }
}
