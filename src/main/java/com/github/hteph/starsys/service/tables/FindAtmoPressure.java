package com.github.hteph.starsys.service.tables;

import com.github.hteph.starsys.service.objects.AtmosphericGases;
import com.github.hteph.utils.Dice;

import java.util.Set;

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

        switch (Dice._2d6() + mod) {
            case 2:
                pressure = (Dice._2d6()) * 0.005;
                break;
            case 3:
                pressure = (Dice._2d6()) * 0.01;
                break;
            case 4:
            case 5:
                pressure = (Dice._2d6()) * 0.05;
                break;
            case 6:
            case 7:
                pressure = (Dice._2d6()) * 0.1;
                break;
            case 8:
            case 9:
            case 10:
                pressure = (Dice._2d6()) * 0.2;
                break;
            case 11:
                pressure = (Dice._2d6()) * 0.5;
                break;
            case 12:
                pressure = Dice._2d6();
                break;
            case 13:
                pressure = (Dice._2d6()) * 3;
                break;
            case 14:
                pressure = (Dice._2d6()) * 5;
                break;
            default:
                pressure = (Dice._2d6()) * 0.001;
                break;
        }
        if (atmoshericComposition.isEmpty()) pressure = 0;
        pressure *= mass;
        return pressure;
    }
}