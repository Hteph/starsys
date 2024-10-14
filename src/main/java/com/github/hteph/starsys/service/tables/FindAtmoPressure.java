package com.github.hteph.starsys.service.tables;

import com.github.hteph.starsys.service.objects.AtmosphericGases;
import com.github.hteph.utils.Dice;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindAtmoPressure {

    public static double calculate(String tectonicActivityGroup,
                                   int hydrosphere,
                                   boolean boilingAtmo,
                                   double mass,
                                   Set<AtmosphericGases> atmoshericComposition) {

        // TODO redo this with a better algorithm, binary search and so on so on
        double pressure;
        int mod = 0;
        if (tectonicActivityGroup.equals("Dead")) mod -= 1;
        if (tectonicActivityGroup.equals("Extreme")) mod += 2;
        if (hydrosphere > 0) mod += 1;
        if (boilingAtmo) mod -= 1;

        pressure = switch (Dice._2d6() + mod) {
            case 2 -> (Dice._2d6()) * 0.005;
            case 3 -> (Dice._2d6()) * 0.01;
            case 4, 5 -> (Dice._2d6()) * 0.05;
            case 6, 7 -> (Dice._2d6()) * 0.1;
            case 8, 9, 10 -> (Dice._2d6()) * 0.2;
            case 11 -> (Dice._2d6()) * 0.5;
            case 12 -> Dice._2d6();
            case 13 -> (Dice._2d6()) * 3;
            case 14 -> (Dice._2d6()) * 5;
            default -> (Dice._2d6()) * 0.001;
        };
        if (atmoshericComposition.isEmpty()) pressure = 0;
        pressure *= mass;
        return pressure;
    }
}