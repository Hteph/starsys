package com.github.hteph.utils.enums;

/**
 */
public enum LocomotionModes implements baseEnum {

JUMPER ("Jumper", "Well Skilled and bodily quipped for jumping."),
GLIDER("Glider", "Equipped with wings or flaps to ride air currents and glide short distances."),
FLIER("Flier", "Uses wings or equivalent to get airborne."),
CLIMBER("Climber", "Well Skilled in and bodily equipped for climbing."),
CLINGER("Clinger", "Spend most time attached to a position."),
BRACHIATOR("Brachiator", "Specialised in moving between branches in the tree canopy."),
BURROWER("Burrower", "Lives under the surface and builds tunnels (burrows) to live in"),
AMPHIBIOUS("Amphibious", "Is equally home in water as on land"),
SWIMMER("Swimmer", "Bodily equipped for using swimming as an efficient form of propulsion but not exclusively."),
AQUATIC("Aquatic", "Obligat living in water and can't survive or move outside of it."),
WALKER("Walker", "Movement is optimised for ground locomotion");

private final String name;
private final String description;

        LocomotionModes(String name, String description) {
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
