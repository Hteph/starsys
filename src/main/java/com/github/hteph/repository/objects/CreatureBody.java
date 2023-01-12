package com.github.hteph.repository.objects;



import com.github.hteph.utils.enums.SensorOrgan;
import com.github.hteph.utils.enums.Symmetry;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
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
