package com.github.hteph.starsys.service.objects;

import com.github.hteph.starsys.enums.SegmentType;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class BodySegment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SegmentType segmentType;
    private Limbs limbs;
    @Singular
    private List<String> organs;
}
