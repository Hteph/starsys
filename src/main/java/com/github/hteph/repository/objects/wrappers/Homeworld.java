package com.github.hteph.repository.objects.wrappers;

import com.github.hteph.repository.objects.TemperatureFacts;
import com.github.hteph.utils.enums.Breathing;
import com.github.hteph.utils.enums.HydrosphereDescription;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;

@Data
@Builder
public class Homeworld {
    //datawrapper for creature creation

    private HydrosphereDescription hydrosphereDescription;
    private String name;
    private StellarObjectType stellarObjectType;
    private int hydrosphere;
    private TemperatureFacts temperatureFacts;
    private double gravity;
    private double magneticField;
    private Breathing repsirating;

    public String getNiceGravity(){

        return BigDecimal.valueOf(gravity).round(new MathContext(2)).toPlainString();
    }
}
