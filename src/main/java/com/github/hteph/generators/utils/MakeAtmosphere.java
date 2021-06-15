package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.repository.objects.TemperatureFacts;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.StreamUtilities;
import com.github.hteph.utils.enums.HydrosphereDescription;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.github.hteph.utils.NumberUtilities.sqrt;
import static com.github.hteph.utils.NumberUtilities.squared;

public class MakeAtmosphere {


    public static void checkAtmo(Set<AtmosphericGases> atmoSet, double atmoPressure) {

        if(atmoSet.size()==1) {

            var oxygen = atmoSet.stream().filter(gas -> gas.getName().equals("O2")).findFirst();

            oxygen.ifPresent(o2 -> {
                int percentage = (int) Math.min(30/atmoPressure +Dice.d3(), 45+Dice.d10());
                o2.setPercentageInAtmo(percentage);
                atmoSet.add(AtmosphericGases.builder()
                                            .name("N2")
                                            .percentageInAtmo(100-percentage)
                                            .build());
            });

        }

        var sumOfGasPercentage = atmoSet.stream()
                                        .map(AtmosphericGases::getPercentageInAtmo)
                                        .reduce(0, Integer::sum);
        if(sumOfGasPercentage > 100){

            do{
                atmoSet.forEach(gas -> {
                    int current = gas.getPercentageInAtmo();
                    gas.setPercentageInAtmo(current-1);
                });

            }while(atmoSet.stream()
                          .map(AtmosphericGases::getPercentageInAtmo)
                          .reduce(0, Integer::sum)>100);


        }

        if (sumOfGasPercentage < 100) {

            var currentN2 = atmoSet.stream()
                                   .filter(g -> g.getName().equals("N2"))
                                   .findAny()
                                   .map(AtmosphericGases::getPercentageInAtmo)
                                   .orElse(0);
            var newN2 = AtmosphericGases.builder()
                                        .name("N2")
                                        .percentageInAtmo(currentN2 + 99 - sumOfGasPercentage);
            atmoSet.add(newN2.build());
            atmoSet.add(AtmosphericGases.builder().name("Other").percentageInAtmo(1).build());
        }


    }

    public static void checkHydrographics(HydrosphereDescription hydrosphereDescription,
                                          int hydrosphere,
                                          double atmoPressure,
                                          Planet.PlanetBuilder<?, ? extends Planet.PlanetBuilder<?, ?>> planetBuilder,
                                          int surfaceTemp,
                                          int[] latitudeWinterTemp,
                                          int[] latitudeSummerTemp) {

        if (hydrosphere > 0 && hydrosphereDescription == HydrosphereDescription.ICE_SHEET
                && surfaceTemp + latitudeWinterTemp[4] > 274) {
            //TODO this should be done in a better way
            planetBuilder.hydrosphereDescription(HydrosphereDescription.LIQUID);
        }

        if (hydrosphere > 0
                && surfaceTemp > 274
                && atmoPressure > 0
                && hydrosphereDescription == HydrosphereDescription.LIQUID) {
            if (MakeAtmosphere.isAboveBoilingpoint(surfaceTemp + latitudeWinterTemp[9], atmoPressure)) {

                planetBuilder.hydrosphereDescription(HydrosphereDescription.VAPOR)
                             .hydrosphere(1);
            } else if (MakeAtmosphere.isAboveBoilingpoint(surfaceTemp, atmoPressure)) {
                int[] latitudeForLiquid = findThresholdForLiquid(surfaceTemp, atmoPressure, latitudeWinterTemp, latitudeSummerTemp);
                if (50 * (1 - Math.sin(10 * latitudeForLiquid[0])) < hydrosphere) {
                    planetBuilder.hydrosphere((int) (50 * (1 - Math.sin(10 * latitudeForLiquid[0]))));
                }
                if (latitudeForLiquid[0] > latitudeForLiquid[1]) {
                    planetBuilder.hydrosphereDescription(HydrosphereDescription.BOILING);
                    planetBuilder.description("Storm World");
                }
            }
        }


        if (hydrosphere == 0
                && (hydrosphereDescription == HydrosphereDescription.LIQUID
                || hydrosphereDescription == HydrosphereDescription.ICE_SHEET)) {
            planetBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
        }

        if (atmoPressure == 0 && hydrosphere > 0) {
            planetBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS)
                         .hydrosphere(0);
        }
    }

    private static int[] findThresholdForLiquid(int surfaceTemp, double atmoPressure, int[] latitudeWinterTemp, int[] latitudeSummerTemp) {
        int[] polarSeaLimit = new int[2];
        for (int i = 0; i < latitudeWinterTemp.length; i++) {

            if (!MakeAtmosphere.isAboveBoilingpoint(surfaceTemp + latitudeWinterTemp[i], atmoPressure)) {
                polarSeaLimit[0] = i;
                break;
            }
        }
        for (int i = 0; i < latitudeSummerTemp.length; i++) {

            if (!MakeAtmosphere.isAboveBoilingpoint(surfaceTemp + latitudeSummerTemp[i], atmoPressure)) {
                polarSeaLimit[1] = i;
                break;
            }
        }

        return polarSeaLimit;
    }

    public static int adjustForOxygen(double atmoPressure, Set<AtmosphericGases> atmosphericComposition) {

        Map<String, AtmosphericGases> atmoMap = atmosphericComposition
                .stream()
                .collect(Collectors.toMap(AtmosphericGases::getName, x -> x));

        int oxygenMax = Math.min(18 + Dice.d10(), (int) ((Dice._3d6() * 2) / atmoPressure)); //This could be a bit more involved and interesting

        int oxygenPercentage =0;

        if (atmoMap.containsKey("CO2")) {
            if (atmoMap.get("CO2").getPercentageInAtmo() > oxygenMax) {
                oxygenPercentage = oxygenMax;
                AtmosphericGases co2 = atmoMap.get("CO2");
                atmoMap.remove("CO2");
                atmoMap.put("O2", AtmosphericGases.builder().name("O2")
                                                  .percentageInAtmo(oxygenMax)
                                                  .build());
                //perhaps the remnant CO should be put in as N2 instead?
                atmoMap.put("CO2", AtmosphericGases.builder()
                                                   .name("CO2")
                                                   .percentageInAtmo(co2.getPercentageInAtmo() - oxygenMax)
                                                   .build());

            } else {
                AtmosphericGases co2 = atmoMap.get("CO2");
                oxygenPercentage = co2.getPercentageInAtmo();
                atmoMap.remove("CO2");
                atmoMap.put("O2", AtmosphericGases.builder()
                                                  .name("O2")
                                                  .percentageInAtmo(oxygenPercentage)
                                                  .build());

            }
        } else { //if no CO2 we just find the largest and take from that

            var maxGas = StreamUtilities.findMaxInMap(atmoMap);

            if (maxGas != null) {
                if (maxGas.getPercentageInAtmo() < oxygenMax) {
                    oxygenPercentage = maxGas.getPercentageInAtmo();
                    atmoMap.put("O2", AtmosphericGases.builder()
                                                      .name("O2")
                                                      .percentageInAtmo(oxygenPercentage)
                                                      .build());

                } else {
                    oxygenPercentage =oxygenMax;
                    atmoMap.put("O2", AtmosphericGases.builder()
                                                      .name("O2")
                                                      .percentageInAtmo(oxygenMax)
                                                      .build());
                    atmoMap.put(maxGas.getName(), AtmosphericGases.builder()
                                                               .name("O2")
                                                               .percentageInAtmo(maxGas.getPercentageInAtmo() -
                                                                                         oxygenMax)
                                                               .build());
                }
            }
        }

        removeCombustibles(atmoMap);
        atmosphericComposition.clear();

        atmosphericComposition.addAll(atmoMap.values());
        return oxygenPercentage;
    }

    public static int findTheHydrosphere(HydrosphereDescription hydrosphereDescription, int radius) {

        //         zeroHydro = () -> 0;
        //         superficialHydro = () -> Dice.d10()/2;
        //         vLowHydro = Dice::d10;
        //         lowHydro = () -> Dice.d10() + 10;
        //         mediumHydro = () -> Dice.d20() + 20;
        //         highHydro = () -> Dice.d20()+ Dice.d20()+ Dice.d20() +37;
        //         vHighHydro = () -> 100;

        List<Supplier<Integer>> hydroList = Arrays.asList(() -> Dice.d10() / 2,
                                                          () -> Dice.d10() / 2 + 5,
                                                          () -> Dice.d10() + 10,
                                                          () -> Dice.d20() + 20,
                                                          () -> Dice.d20() + Dice.d20() + Dice.d20() + 37,
                                                          () -> 100);
        int[] wetSmallPlanetHydro = {2, 5, 9, 10, 12, 13};
        int[] wetMediumPlanetHydro = {2, 4, 7, 9, 11, 12};
        int[] wetLargePlanetHydro = {0, 2, 3, 4, 7, 12};

        Integer tempHydro = 0;

        if (hydrosphereDescription.equals(HydrosphereDescription.LIQUID)
                || hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)) {
            if (radius < 2000) {
                tempHydro = TableMaker.makeRoll(Dice._2d6(), wetSmallPlanetHydro, hydroList).get();
            } else if (radius < 4000) {
                tempHydro = TableMaker.makeRoll(Dice._2d6(), wetMediumPlanetHydro, hydroList).get();
            } else if (radius < 7000) {
                tempHydro = TableMaker.makeRoll(Dice._2d6(), wetLargePlanetHydro, hydroList).get();
            }
        } else if (hydrosphereDescription.equals(HydrosphereDescription.CRUSTAL)) tempHydro = 100;
        else if (hydrosphereDescription.equals(HydrosphereDescription.REMNANTS)) tempHydro = Dice.d6() / 2;

        tempHydro = Math.min(100, tempHydro);

        return tempHydro;
    }

    public static HydrosphereDescription findHydrosphereDescription(boolean InnerZone, int baseTemperature) {
        HydrosphereDescription tempHydroD;
        if (!InnerZone) {
            tempHydroD = HydrosphereDescription.CRUSTAL;
        } else if (baseTemperature > 500) {
            tempHydroD = HydrosphereDescription.NONE;
        } else if (baseTemperature > 370) {
            tempHydroD = HydrosphereDescription.VAPOR;
        } else if (baseTemperature > 245) {
            tempHydroD = HydrosphereDescription.LIQUID;
        } else {
            tempHydroD = HydrosphereDescription.ICE_SHEET;
        }
        return tempHydroD;
    }

    public static double getWaterVaporFactor(int baseTemperature, HydrosphereDescription hydrosphereDescription, int hydrosphere) {
        double waterVaporFactor;
        if (hydrosphereDescription.equals(HydrosphereDescription.LIQUID)
                || hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)) {
            waterVaporFactor = (int) Math.max(0, (baseTemperature - 240) / 100.0 * hydrosphere * (Dice.aLotOfd3(2) - 1));
        } else {
            waterVaporFactor = 0;
        }
        return waterVaporFactor;
    }

    public static void removeCombustibles(Map<String, AtmosphericGases> atmoMap) {
        int removedpercentages = 0;
        if (atmoMap.containsKey("CH4")) {
            int burned = atmoMap.get("CH4").getPercentageInAtmo();
            atmoMap.remove("CH4");
            atmoMap.put("CO2", AtmosphericGases.builder()
                                              .name("CO2")
                                              .percentageInAtmo((int)(burned/2.0))
                                              .build());
            removedpercentages += (int)(burned/2.0);
        }
        if (atmoMap.containsKey("H2")) {
            removedpercentages += atmoMap.get("H2").getPercentageInAtmo();
            atmoMap.remove("H2");
        }
//        There are indications that ammonia may be present in an oxygen atmosphere
        // so this is removed for now
//        if (atmoMap.containsKey("NH3")) {
//            removedpercentages += atmoMap.get("NH3").getPercentageInAtmo();
//            atmoMap.remove("NH3");
//        }
        if (removedpercentages > 0) {
            if (atmoMap.containsKey("N2")) {
                removedpercentages += atmoMap.get("N2").getPercentageInAtmo();
                atmoMap.remove("N2");
            }
            atmoMap.put("N2", AtmosphericGases.builder()
                                              .name("N2")
                                              .percentageInAtmo(removedpercentages)
                                              .build());
        }
    }

    public static double findGreenhouseGases(Set<AtmosphericGases> atmosphericComposition,
                                             double atmoPressure,
                                             int baseTemperature,
                                             HydrosphereDescription hydrosphereDescription,
                                             int hydrosphere) {
        double tempGreenhouseGasEffect = 0;

        for (AtmosphericGases gas : atmosphericComposition) {

            switch (gas.getName()) {
                case "CO2":
                    tempGreenhouseGasEffect += gas.getPercentageInAtmo() * atmoPressure;
                    break;
                case "CH4":
                    tempGreenhouseGasEffect += gas.getPercentageInAtmo() * atmoPressure * 4;
                    break;
                case "SO2":
                case "NH3":
                case "NO2":
                case "H2S":
                    tempGreenhouseGasEffect += gas.getPercentageInAtmo() * atmoPressure * 8;
                    break;
                case "H2SO4":
                    tempGreenhouseGasEffect += gas.getPercentageInAtmo() * atmoPressure * 16;
                    break;
                default:
                    tempGreenhouseGasEffect += gas.getPercentageInAtmo() * atmoPressure * 0;
                    break;
            }
        }

        var waterVaporFactor = MakeAtmosphere.getWaterVaporFactor(baseTemperature, hydrosphereDescription, hydrosphere);

        var random = Dice.aLotOfd3(2) - 1;


        return sqrt(1 + atmoPressure * 0.01 * random
                + tempGreenhouseGasEffect * 0.1
                + waterVaporFactor * 0.1);
    }



    public static boolean isAboveBoilingpoint(int temperature, double pressure) {
//https://www.ajdesigner.com/phpvaporpressure/water_vapor_pressure_equation.php
        if (temperature < 274) return false;

        if (temperature > 640) return true;

        double A;
        double B;
        double C;

        if (temperature < 374) {
            A = 8.07131;
            B = 1730.63;
            C = 233.426;
        } else {
            A = 8.14019;
            B = 1810.94;
            C = 244.485;
        }

        return temperature > (B/(A-Math.log10(pressure)))-C;

    }

    @SuppressWarnings("rawtypes")
    static public Set<AtmosphericGases> createPlanetary(Star star,
                                                            int baseTemperature,
                                                            String tectonicActivityGroup,
                                                            double radius,
                                                            double gravity,
                                                            double magneticField,
                                                        Planet.PlanetBuilder planet) {
        Set<String> makeAtmoshpere = new HashSet<>();

        startAtmosphereFromTemperature(baseTemperature, tectonicActivityGroup, makeAtmoshpere);

        if (tectonicActivityGroup.equals("Extreme") && Dice.d6() < 4) {
            makeAtmoshpere.add("SO2");
            makeAtmoshpere.add("H2S");
            if (Dice.d6() < 2) makeAtmoshpere.add("H2SO4");
        }

        boolean boilingAtmo = whatGasesStaysInTheAtmosphere(baseTemperature, radius, gravity, makeAtmoshpere);

        planet.boilingAtmo(boilingAtmo);

        howDoTheStarlightAffectTheAtmosphere(star, baseTemperature, makeAtmoshpere, magneticField);

        Set<AtmosphericGases> atmosphere = new HashSet<>();

        if (!makeAtmoshpere.isEmpty()) {

           atmosphere =  setAtmosphericGasesConcentrations(makeAtmoshpere);
        }
        return atmosphere;
    }

    private static Set<AtmosphericGases> setAtmosphericGasesConcentrations(Set<String> makeAtmoshpere) {

        //This should really be done with a bit more thought and just not random.

        int[] part = new int[makeAtmoshpere.size()];
        part[0] = (5 * (Dice._2d6()) + 33); //size of primary gas
        int percentage = 100 - part[0];

        for (int i = 1; i < part.length; i++) {
            part[i] = percentage / 2;
            percentage -= part[i];
        }

        part[0] += percentage; //whats left is added to primary gas


        ArrayList<String> randGas = new ArrayList<>();
        if(makeAtmoshpere.contains("N2")){
            makeAtmoshpere.remove("N2");

            randGas.addAll(makeAtmoshpere);
            Collections.shuffle(randGas);
            randGas.add(0,"N2");

        } else {
            randGas.addAll(makeAtmoshpere);

        }

        Set<AtmosphericGases> atmo = new HashSet<>();
        for (int i = 0; i < part.length; i++) {
            atmo.add(AtmosphericGases.builder()
                                     .name(randGas.get(i))
                                     .percentageInAtmo(part[i])
                                     .build());
        }

        return  atmo;
    }

    private static void howDoTheStarlightAffectTheAtmosphere(Star star, int baseTemperature, Set<String> makeAtmoshpere, double magneticField) {
        var magneticFieldFactor = 1+squared(magneticField)/100;

        if ((star.getClassification().contains("A")
                || star.getClassification().contains("B"))
                && baseTemperature > 150 * magneticFieldFactor) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("F") && baseTemperature > 180 * magneticFieldFactor ) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("G") && baseTemperature > 230 * magneticFieldFactor) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if (star.getClassification().contains("K") && baseTemperature > 250 * magneticFieldFactor) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
        if ((star.getClassification().contains("M")) && baseTemperature > 270 * magneticFieldFactor) {
            makeAtmoshpere.remove("H2O");
            makeAtmoshpere.remove("NH3");
            makeAtmoshpere.remove("CH4");
            makeAtmoshpere.remove("H2S");
        }
    }

    private static boolean whatGasesStaysInTheAtmosphere(int baseTemperature, double radius, double gravity, Set<String> makeAtmoshpere) {
        double retainedGases = 0.02783 * baseTemperature / squared(sqrt((19600 * gravity * radius)) / 11200);
        boolean boilingAtmo = false;
//Rewrite this by assigning gasses as Enums with molecular weight and so on
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
            case 3:
            case 4:
            case 5:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("Ne");
                break;
            case 6:
            case 7:
            case 8:
                makeAtmoshpere.add("He");
                makeAtmoshpere.add("H2");
                break;
            case 9:
            case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("H2");
                break;
            case 11:
            case 12:
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
            case 3:
            case 4:
            case 5:
                makeAtmoshpere.add("H2");
                makeAtmoshpere.add("He");
                break;
            case 6:
            case 7:
            case 8:
                makeAtmoshpere.add("H2");
                makeAtmoshpere.add("He");
                makeAtmoshpere.add("N2");
                break;
            case 9:
            case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:
            case 12:
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
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9:
            case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:
            case 12:
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
            case 4:
            case 5:
                makeAtmoshpere.add("H2O");//Obs just adding water to below
            case 6:
            case 7:
            case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9:
            case 10:
                makeAtmoshpere.add("NO2");
                makeAtmoshpere.add("SO2");
                break;
            case 11:
            case 12:
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
            case 4:
            case 5:
                makeAtmoshpere.add("H2O");//Obs just adding water to below
            case 6:
            case 7:
            case 8:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CO2");
                break;
            case 9:
            case 10:
                makeAtmoshpere.add("N2");
                makeAtmoshpere.add("CH4");
                break;
            case 11:
            case 12:
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