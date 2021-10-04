package com.github.hteph.repository.objects;



import com.github.hteph.utils.enums.SensorOrgan;
import com.github.hteph.utils.enums.Symmetry;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CreatureBody {

    private SensorOrgan sensorOrgan;
    private Symmetry bodySymmetry;
    private int limbPerSegment;
    private List<BodySegment> bodyStructure;

}
