package com.github.hteph.repository.objects;

/**
 * @author Mikael Hansson {@literal <mailto:mikael.hansson@so4it.com/>}
 */
public class Limbs {

    private boolean limbSegment;
    private int dexterityBonus;
    private int strengthBonus;

    public Limbs(boolean isLimbSegment){
        this.limbSegment = isLimbSegment;
    }

    public boolean isLimbSegment() {
        return limbSegment;
    }

    public void setLimbSegment(boolean limbSegment) {
        this.limbSegment = limbSegment;
    }

    public int getDexterityBonus() {
        return dexterityBonus;
    }

    public void setDexterityBonus(int dexterityBonus) {
        this.dexterityBonus = dexterityBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public void setStrengthBonus(int strengthBonus) {
        this.strengthBonus = strengthBonus;
    }
}
