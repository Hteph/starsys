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
        if (age < 2) return false;

        double lifeIndex = 0;

        if (baseTemperature < 100 || baseTemperature > 450) lifeIndex -= 5;
        else if (baseTemperature < 270 || baseTemperature > 350) lifeIndex -= 4;
        else lifeIndex += 4;
        System.out.println("After temp life Index="+lifeIndex +" basetemp = "+baseTemperature);

        if (atmoPressure < 0.2) lifeIndex -= 10;
        else if (atmoPressure > 5) lifeIndex -= 1;
        else lifeIndex +=1;
        System.out.println("After atmo life Index="+lifeIndex + "pressure = "+ atmoPressure);

        if (hydrosphere < 1) lifeIndex -= 5;
        else if (hydrosphere < 5) lifeIndex += 1;
        else lifeIndex += 4;

        System.out.println("After hydro life Index="+lifeIndex +"Hydro = "+hydrosphere);

        if (baseTemperature<270
                && atmoshericComposition.stream().anyMatch(s -> s.getName().equals("NH3") && Dice.d6() < 4)){
            //TODO rework this to work  in the same way as Oxygen?
            lifeIndex += 4;
        }
System.out.println("End life Index="+lifeIndex);
        return lifeIndex > 0; //Nod to Gaia-theory, if there is any chance of life it will aways be life present
    }

    public static Breathing findLifeType(Set<AtmosphericGases> atmoshericComposition, double age) {


        //TODO Allow for alternate gases such as Cl2
        return atmoshericComposition.stream()
                                    .map(AtmosphericGases::getName)
                                    .anyMatch(b -> b.equals("NH3"))
                ? Breathing.AMMONIA
                : age < 3 ? Breathing.PROTO : Breathing.OXYGEN;
    }
}