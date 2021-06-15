package com.github.hteph.utils.enums;

/**
 * @author Mikael Hansson {@literal <mailto:mikael.hansson@so4it.com/>}
 */
public enum Symmetry implements baseEnum{

    NONE("No symmetry","The lifeform lacks any symmetry in placement of limbs and organ locations. This makes it difficult to aim for vitals.",0),
    BILATERAL("Bilateral","Has two \"sides\" that sturcurally mirrors each other",2),
    RADIAL("Radial","Has a basic structure with either a multitude of \"sides\" or a undefined structure distributed around a center.",9),
    TRILATERAL("Trilateral","Limbs and body structures are ",3),
    QUADRAL("Quadratic","Has four well defined limb attachment points.",4),
    PENTRADAL("Pentagonal","Has a body with five limb directed \"sides\".",5);

    private final String name;
    private final String description;
    private final int sides;



    Symmetry(String name, String description, int sides) {
        this.name =name;
        this.description = description;
        this.sides = sides;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getSides() {return sides;}
}
