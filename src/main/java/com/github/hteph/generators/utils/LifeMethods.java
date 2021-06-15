package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.tables.TectonicActivityTable;
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
                                   double age, double magneticField,
                                   String tectonicActivityGroup) {

        //setting a limit on how fast life can develop
        if (age < 2) return false;
        if (atmoPressure == 0) return false; //TODO Machine worlds and vacuum beings?

        double lifeIndex = 0;

        if (baseTemperature < 220 || baseTemperature > 420) lifeIndex -= 7;
        else if (baseTemperature < 270){
            if (tectonicActivityGroup.contains("dead")) lifeIndex -=5;
            else lifeIndex -= 2;
        }else if( baseTemperature > 350) lifeIndex -= 3;
        else lifeIndex += 4;
System.out.println("Index after temp="+lifeIndex +", temp="+baseTemperature);
        if (atmoPressure < 0.2) lifeIndex -= 5;
        else if (atmoPressure > 5) lifeIndex -= 1;
        else lifeIndex +=1;
        System.out.println("Index after pressure="+lifeIndex);

        if (hydrosphere < 1) lifeIndex -= 5;
        else if (hydrosphere < 5) lifeIndex += 1;
        else lifeIndex += 4;
        System.out.println("Index after hydro="+lifeIndex + ", hydro = "+ hydrosphere);

        if(magneticField < 0.3) lifeIndex += - 3;
        else if (magneticField > 5){
            //Perhaps exotic beings living of the Magnetic field energy?
            lifeIndex += 1;
        }
        System.out.println("Index after magnetfield="+lifeIndex +", magnetic = "+magneticField);


        if (atmoshericComposition.stream().anyMatch(s -> s.getName().equals("NH3"))){
            //TODO rework this to work  in the same way as Oxygen?
            lifeIndex += 4;
        }
        System.out.println("Index after ammonia="+lifeIndex);

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