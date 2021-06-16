package com.github.hteph.repository.objects;



import com.github.hteph.utils.enums.Symmetry;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class CreatureBody {

    private Symmetry bodySymmetry;
    private int limbPerSegment;
    private ArrayList<BodySegment> limbs;

}
