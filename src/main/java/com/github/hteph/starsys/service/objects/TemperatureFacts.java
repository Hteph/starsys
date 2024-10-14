package com.github.hteph.starsys.service.objects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemperatureFacts {

    private int surfaceTemp;
    private int[] rangeBandTemperature;
    private int[] rangeBandTempSummer;
    private int[] rangeBandTempWinter;
    private Variation dayNightVariation;
    private Variation eccentricityVariation;

    @Data
    @Builder
    static public class Variation {
        int max;
        int min;
    }
}
