package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.utils.Dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class MakeAtmosphere {


    public static boolean isAboveBoilingpoint(int temperature, double pressure){

        if(temperature<274) return false;

        if(temperature>640) return true;

        double A;
        double B;
        double C;

        if(temperature<374){
            A=8.07131;
            B=1730.63;
            C=233.426;
        } else {
            A=8.14019;
            B=1810.94;
            C=244.485;
        }

        return pressure>Math.pow(10,A-(B/(C+(temperature-274))));
    }

    public static void checkAtmo(Set<AtmosphericGases> atmoSet) {
        var sumOfGasPercentage = atmoSet.stream()
                                        .map(AtmosphericGases::getPercentageInAtmo)
                                        .reduce(0, Integer::sum);

        if (sumOfGasPercentage < 100) {

            var currentN2 = atmoSet.stream()
                                   .filter(g->g.getName().equals("N2"))
                                   .findAny()
                                   .map(AtmosphericGases::getPercentageInAtmo)
                                   .orElse(0);
            var newN2 = AtmosphericGases.builder()
                                        .name("N2")
                                        .percentageInAtmo(currentN2 + 100 - sumOfGasPercentage);
            atmoSet.add(newN2.build());

        }
    }

    @SuppressWarnings("rawtypes")
    static public TreeSet<AtmosphericGases> createPlanetary(Star star,
                                                 int baseTemperature,
                                                 String tectonicActivityGroup,
                                                 double radius,
                                                 double gravity,
                                                 Planet.PlanetBuilder planet) {
        Set<String> makeAtmoshpere = new TreeSet<>();
        TreeSet<AtmosphericGases> atmoArray = new TreeSet<>(new AtmosphericGases.atmoCompositionComparator());

        startAtmosphereFromTemperature(baseTemperature, tectonicActivityGroup, makeAtmoshpere);

        if (tectonicActivityGroup.equals("Extreme") && Dice.d6() < 4) {
            makeAtmoshpere.add("SO2");
            makeAtmoshpere.add("H2S");
            if (Dice.d6() < 2) makeAtmoshpere.add("H2SO4");
        }

        boolean boilingAtmo = whatGasesStaysInTheAtmosphere(baseTemperature, radius, gravity, makeAtmoshpere);

        planet.boilingAtmo(boilingAtmo);

        howDoTheStarlightAffectTheAtmosphere(star, baseTemperature, makeAtmoshpere);

        if (!makeAtmoshpere.isEmpty()) {

            setAtmosphericGasesConcentrations(makeAtmoshpere, atmoArray);
        }
        return atmoArray;
    }

    private static void setAtmosphericGasesConcentrations(Set<String> makeAtmoshpere, TreeSet<AtmosphericGases> atmoArray) {

        //This should really be done with a bit more thought and just not random.

        int[] part = new int[makeAtmoshpere.size()];
        part[0] = (5 * (Dice._2d6()) + 30); //size of primary gas
        int percentage = 100 - part[0];

        for (int i = 1; i < part.length; i++) {
            part[i] = percentage / 2;
            percentage -= part[i];
        }

        part[0] += percentage; //whats left is added to primary gas
        ArrayList<String> randGas = new ArrayList<>(makeAtmoshpere);
        Collections.shuffle(randGas);

        for (int i = 0; i < part.length; i++) {
            atmoArray.add(AtmosphericGases.builder()
                                          .name(randGas.get(i))
                                          .percentageInAtmo(part[i])
                                          .build());
        }
    }

    private static void howDoTheStarlightAffectTheAtmosphere(Star star, int baseTemperature, Set<String> makeAtmoshpere) {
        if ((star.getClassification().contains("A")
                || star.getClassification().contains("B"))
                && baseTemperature > 150) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("F") && baseTemperature > 180) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("G") && baseTemperature > 230) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("K") && baseTemperature > 250) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if ((star.getClassification().contains("M")) && baseTemperature > 270) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
    }

    private static boolean whatGasesStaysInTheAtmosphere(int baseTemperature, double radius, double gravity, Set<String> makeAtmoshpere) {
        double retainedGases = 0.02783 * baseTemperature / Math.pow(Math.pow((19600 * gravity * radius), 0.5) / 11200, 2);
        boolean boilingAtmo = false;

        if (retainedGases > 2) boilingAtmo = makeAtmoshpere.remove("H2");
        if (retainedGases > 4) boilingAtmo = makeAtmoshpere.remove("He");
        if (retainedGases > 16) boilingAtmo = makeAtmoshpere.remove("CH4");
        if (retainedGases > 17) boilingAtmo = makeAtmoshpere.remove("NH3");
        if (retainedGases > 17) boilingAtmo = makeAtmoshpere.remove("H2O");
        if (retainedGases > 20) boilingAtmo = makeAtmoshpere.remove("Ne");
        if (retainedGases > 28) boilingAtmo = makeAtmoshpere.remove("N2");
        if (retainedGases > 28) boilingAtmo = makeAtmoshpere.remove("CO");
        if (retainedGases > 30) boilingAtmo = makeAtmoshpere.remove("NO");
        if (retainedGases > 34) boilingAtmo = makeAtmoshpere.remove("H2S");
        if (retainedGases > 38) boilingAtmo = makeAtmoshpere.remove("F2");
        if (retainedGases > 40) boilingAtmo = makeAtmoshpere.remove("Ar");
        if (retainedGases > 44) boilingAtmo = makeAtmoshpere.remove("CO2");
        if (retainedGases > 46) boilingAtmo = makeAtmoshpere.remove("NO2");
        if (retainedGases > 62) boilingAtmo = makeAtmoshpere.remove("SO2");
        if (retainedGases > 70) boilingAtmo = makeAtmoshpere.remove("Cl2");
        if (retainedGases > 98) boilingAtmo = makeAtmoshpere.remove("H2SO4");
        return boilingAtmo;
    }

    private static void startAtmosphereFromTemperature(int baseTemperature, String tectonicActivityGroup, Set<String> makeAtmoshpere) {
        if (baseTemperature > 400) {
            makeHot(tectonicActivityGroup, makeAtmoshpere);
        } else if (baseTemperature > 240) {
            makeMedium(makeAtmoshpere);
        } else if (baseTemperature > 150) {
            makeChilly(makeAtmoshpere);
        } else if (baseTemperature > 50) {
            makeCold(makeAtmoshpere);
        } else {
            makeFrozen(makeAtmoshpere);
        }
    }

    private static void makeFrozen(Set<String> makeAtmoshpere) {
        switch (Dice.d6() + Dice.d6()) {
            case 2:
                makeAtmoshpere.add("N2");
                if (Dice.d6() < 4) makeAtmoshpere.add("CO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("NO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("SO2");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ne");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ar");
                if (Dice.d6() < 2) makeAtmoshpere.add("He");
                if (Dice.d6() < 2) makeAtmoshpere.add("H2");
                if (Dice.d6() < 2) makeAtmoshpere.add("NH3");

                break;
            case 3:case 4: case 5:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("Ne");
                break;
            case 6:case 7: case 8:
                makeAtmoshpere.add("He");
                makeAtmoshpere.add("H2");
                break;
            case 9: case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("H2");
                break;
            case 11:case 12:
                makeAtmoshpere.add("He");
                break;
            default:
                makeAtmoshpere.add("N2");
                break;
        }
    }

    private static void makeCold(Set<String> makeAtmoshpere) {
        switch (Dice.d6() + Dice.d6()) {
            case 2:
                makeAtmoshpere.add("N2");
                if (Dice.d6() < 4) makeAtmoshpere.add("CO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("NO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("SO2");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ne");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ar");
                if (Dice.d6() < 2) makeAtmoshpere.add("He");
                if (Dice.d6() < 2) makeAtmoshpere.add("H2");
                if (Dice.d6() < 2) makeAtmoshpere.add("CO");
                if (Dice.d6() < 2) makeAtmoshpere.add("NH3");
                break;
            case 3:case 4: case 5:
                makeAtmoshpere.add("H2");
                makeAtmoshpere.add("He");
                break;
            case 6:case 7: case 8:
                makeAtmoshpere.add("H2");
                makeAtmoshpere.add("He");
                makeAtmoshpere.add("N2");
                break;
            case 9: case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:case 12:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO");
                break;
            default:
                makeAtmoshpere.add("N2");
                break;
        }
    }

    private static void makeChilly(Set<String> makeAtmoshpere) {
        switch (Dice.d6() + Dice.d6()) {
            case 2:
                makeAtmoshpere.add("N2");
                if (Dice.d6() < 4) makeAtmoshpere.add("CO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("NO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("SO2");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ne");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ar");
                if (Dice.d6() < 2) makeAtmoshpere.add("He");
                if (Dice.d6() < 2) makeAtmoshpere.add("H2");
                if (Dice.d6() < 2) makeAtmoshpere.add("NH3");
                break;
            case 3:
                makeAtmoshpere.add("CO2");
                break;
            case 4:case 5:case 6:case 7: case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9: case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:case 12:
                makeAtmoshpere.add("H2");
                makeAtmoshpere.add("He");
                break;
            default:
                makeAtmoshpere.add("N2");
                break;
        }
    }

    private static void makeHot(String tectonicActivityGroup, Set<String> makeAtmoshpere) {
        switch (Dice.d6() + Dice.d6()) {
            case 2:
                makeAtmoshpere.add("N2");
                if (Dice.d6() < 4) makeAtmoshpere.add("CO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("NO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("SO2");
                if (Dice.d6() < 2) makeAtmoshpere.add("F2");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ne");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ar");
                if (tectonicActivityGroup.equals("Extreme")) {
                    makeAtmoshpere.add("SO2");
                    makeAtmoshpere.add("H2S");
                }
                break;
            case 3:
                makeAtmoshpere.add("CO2");
                break;
            case 4:case 5:
                makeAtmoshpere.add("H2O");//Obs just adding water to below
            case 6: case 7:case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9: case 10:
                makeAtmoshpere.add("NO2");
                makeAtmoshpere.add("SO2");
                break;
            case 11: case 12:
                makeAtmoshpere.add("SO2");
                break;
            default:
                makeAtmoshpere.add("N2");
                break;
        }
    }

    private static void makeMedium(Set<String> makeAtmoshpere) {
        switch (Dice.d6() + Dice.d6()) {
            case 2:
                makeAtmoshpere.add("N2");
                if (Dice.d6() < 4) makeAtmoshpere.add("CO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("NO2");
                if (Dice.d6() < 4) makeAtmoshpere.add("SO2");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ne");
                if (Dice.d6() < 2) makeAtmoshpere.add("Ar");
                if (Dice.d6() < 2) makeAtmoshpere.add("He");
                if (Dice.d6() < 2) makeAtmoshpere.add("NH3");
                break;
            case 3:
                makeAtmoshpere.add("CO2");
                break;
            case 4: case 5:
                makeAtmoshpere.add("H2O");//Obs just adding water to below
            case 6:case 7:case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9:case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:case 12:
                makeAtmoshpere.add("CO2");
                makeAtmoshpere.add("CH4");
                makeAtmoshpere.add("NH3");
                break;
            default:
                makeAtmoshpere.add("N2");
                break;
        }
    }
}