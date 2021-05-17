package com.github.hteph.repository.objects;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TemperatureFacts {

    private int surfaceTemp;
    private int[] rangeBandTemperature;
    private int[] rangeBandTempSummer;
    private int[] rangeBandTempWinter;
    private BigDecimal nightTempMod;
    private BigDecimal dayTempMod;
}
