package com.github.hteph.repository.objects;



import com.github.hteph.utils.enums.Symmetry;

import java.util.ArrayList;

/**

 */
public class CreatureBody {

    private Symmetry bodySymmetry;
    private int limbSegments;
    private ArrayList<Limbs> limbs;

    public CreatureBody(){}

    public int getLimbSegments() {
        return limbSegments;
    }

    public void setLimbSegments(int limbSegments) {
        this.limbSegments = limbSegments;
    }

    public ArrayList<Limbs> getLimbs() {
        return limbs;
    }

    public void setLimbs(ArrayList<Limbs> limbs) {
        this.limbs = limbs;
    }

    public Symmetry getBodySymmetry() {
        return bodySymmetry;
    }

    public void setBodySymmetry(Symmetry bodySymmetry) {
        this.bodySymmetry = bodySymmetry;
    }
}
