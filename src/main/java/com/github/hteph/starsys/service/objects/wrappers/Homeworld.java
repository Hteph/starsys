package com.github.hteph.starsys.service.objects.wrappers;

import com.github.hteph.starsys.service.objects.TemperatureFacts;
import com.github.hteph.starsys.enums.HydrosphereDescription;
import com.github.hteph.starsys.enums.StellarObjectType;
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

    public String getNiceGravity(){

        return BigDecimal.valueOf(gravity).round(new MathContext(2)).toPlainString();
    }
}
