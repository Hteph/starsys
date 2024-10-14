package com.github.hteph.starsys.service.objects;



import com.github.hteph.starsys.enums.SensorOrgan;
import com.github.hteph.starsys.enums.Symmetry;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CreatureBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SensorOrgan sensorOrgan;
    private Symmetry bodySymmetry;
    private int limbPerSegment;
    private List<BodySegment> bodyStructure;

}
