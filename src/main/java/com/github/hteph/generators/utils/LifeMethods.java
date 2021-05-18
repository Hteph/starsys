package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.Breathing;

import java.util.Set;

public class LifeMethods {

    /*TODO
     * This should be reworked (in conjuction with atmo) to remove CL and F from naturally occuring and instead
     * treat them similar to Oxygen. Also the Ammonia is dependent on free water as written right now
     */

    public static boolean testLife(int baseTemperature,
                                   double atmoPressure,
                                   int hydrosphere,
                                   Set<AtmosphericGases> atmoshericComposition,
                                   double age) {

        //setting a limit on how fast life can develop
        if(age<2) return false;

        double lifeIndex = 0;

        if (baseTemperature < 100 || baseTemperature > 450) lifeIndex -= 5;
        else if (baseTemperature < 250 || baseTemperature > 350) lifeIndex -= 1;
        else lifeIndex += 3;

        if (atmoPressure < 0.1) lifeIndex -= 10;
        else if (atmoPressure > 5) lifeIndex -= 1;

        if (hydrosphere < 1) lifeIndex -= 3;
        else if (hydrosphere > 3) lifeIndex += 1;

        if (atmoshericComposition.stream().anyMatch(s -> s.getName().equals("NH3") && Dice.d6() < 3)) lifeIndex += 4;

        return lifeIndex > 0; //Nod to Gaia-theory, if there is any chance of life it will aways be life present
    }

    public static Breathing findLifeType(Set<AtmosphericGases> atmoshericComposition, double age) {
        //TODO Allow for alternate gases such as Cl2
        return atmoshericComposition.stream()
                                    .map(AtmosphericGases::getName)
                                    .anyMatch(b -> b.equals("NH3"))
                ? Breathing.AMMONIA
                : age<3?Breathing.PROTO:Breathing.OXYGEN;
    }
}