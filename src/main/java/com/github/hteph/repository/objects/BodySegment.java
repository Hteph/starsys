package com.github.hteph.repository.objects;

import com.github.hteph.utils.enums.SegmentType;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class BodySegment {

    private SegmentType segmentType;
    private Limbs limbs;
    @Singular
    private List<String> organs;
}
