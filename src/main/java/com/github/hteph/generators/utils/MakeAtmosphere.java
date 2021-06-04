package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.repository.objects.TemperatureFacts;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.HydrosphereDescription;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.github.hteph.utils.NumberUtilities.sqrt;

public class MakeAtmosphere {

    private static final String SUBSCRIPT_TWO = "\u2082";

    public static void checkAtmo(TreeSet<AtmosphericGases> atmoSet) {
        atmoSet.forEach(gas -> System.out.println("Start Gas: "+gas.getName()+" "+gas.getPercentageInAtmo()+" %"));
        var sumOfGasPercentage = atmoSet.stream()
                                        .map(AtmosphericGases::getPercentageInAtmo)
                                        .reduce(0, Integer::sum);
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
            atmoSet.forEach(gas -> System.out.println("End Gas: "+gas.getName()+" "+gas.getPercentageInAtmo()+" %"));
        }
    }

    public static void checkHydrographics(HydrosphereDescription hydrosphereDescription,
                                           int hydrosphere,
                                           BigDecimal atmoPressure,
                                           Planet.PlanetBuilder<?, ? extends Planet.PlanetBuilder<?, ?>> planetBuilder,
                                           int surfaceTemp,
                                           int[] latitudeWinterTemp,
                                           int[] latitudeSummerTemp) {
        if (hydrosphere > 0
                && surfaceTemp > 274
                && atmoPressure.doubleValue() > 0
                && hydrosphereDescription == HydrosphereDescription.LIQUID) {
            if (MakeAtmosphere.isAboveBoilingpoint(surfaceTemp + latitudeWinterTemp[9], atmoPressure.doubleValue())) {

                planetBuilder.hydrosphereDescription(HydrosphereDescription.VAPOR)
                             .hydrosphere(1);
            } else if (MakeAtmosphere.isAboveBoilingpoint(surfaceTemp, atmoPressure.doubleValue())) {
                int[] latitudeForLiquid = findThresholdForLiquid(surfaceTemp, atmoPressure.doubleValue(), latitudeWinterTemp, latitudeSummerTemp);
                if (50 * (1 - Math.sin(10 * latitudeForLiquid[0])) < hydrosphere) {
                    planetBuilder.hydrosphere((int) (50 * (1 - Math.sin(10 * latitudeForLiquid[0]))));
                }
                if (latitudeForLiquid[0] > latitudeForLiquid[1]) {
                    planetBuilder.hydrosphereDescription(HydrosphereDescription.BOILING);
                    planetBuilder.description("Storm World");
                }
            }
        } else if (hydrosphere > 0 && hydrosphereDescription == HydrosphereDescription.ICE_SHEET
                && surfaceTemp + latitudeWinterTemp[4] > 274) {
            System.out.println("Ice to Liquid, latitude40 winter temp: " + latitudeWinterTemp[4]);
            planetBuilder.hydrosphereDescription(HydrosphereDescription.LIQUID);
        } else if (hydrosphere == 0
                && (hydrosphereDescription == HydrosphereDescription.LIQUID
                || hydrosphereDescription == HydrosphereDescription.ICE_SHEET)) {
            planetBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
        }
        if (atmoPressure.doubleValue() == 0 && hydrosphere > 0) {
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

    public static void adjustForOxygen(double atmoPressure, TreeSet<AtmosphericGases> atmosphericComposition) {

        Map<String, AtmosphericGases> atmoMap = atmosphericComposition
                .stream()
                .collect(Collectors.toMap(AtmosphericGases::getName, x -> x));

        int oxygenMax = Math.max(18+Dice.d10(), (int) (Dice._3d6() * 2 / atmoPressure)); //This could be a bit more involved and interesting

        if (atmoMap.containsKey("CO2")) {
            if (atmoMap.get("CO2").getPercentageInAtmo() > oxygenMax) {
                AtmosphericGases co2 = atmoMap.get("CO2");
                atmoMap.remove("CO2");
                atmoMap.put("O2", AtmosphericGases.builder().name("O2").percentageInAtmo(oxygenMax).build());
                //perhaps the remnant CO should be put in as N2 instead?
                atmoMap.put("CO2", AtmosphericGases.builder()
                                                   .name("CO2")
                                                   .percentageInAtmo(co2.getPercentageInAtmo() - oxygenMax)
                                                   .build());

            } else {
                AtmosphericGases co2 = atmoMap.get("CO2");
                atmoMap.remove("CO2");
                atmoMap.put("O2", AtmosphericGases.builder()
                                                  .name("O"+SUBSCRIPT_TWO)
                                                  .percentageInAtmo(co2.getPercentageInAtmo())
                                                  .build());
            }
        } else { //if no CO2 we just find the largest and take from that
            AtmosphericGases gas = atmosphericComposition.pollFirst();
            if (gas != null) {
                if (gas.getPercentageInAtmo() < oxygenMax) {
                    atmoMap.put("O2", AtmosphericGases.builder()
                                                      .name("O2")
                                                      .percentageInAtmo(gas.getPercentageInAtmo())
                                                      .build());
                } else {
                    atmoMap.put("O2", AtmosphericGases.builder()
                                                      .name("O2")
                                                      .percentageInAtmo(oxygenMax)
                                                      .build());
                    atmoMap.put(gas.getName(), AtmosphericGases.builder()
                                                               .name("O2")
                                                               .percentageInAtmo(gas.getPercentageInAtmo() -
                                                                                         oxygenMax)
                                                               .build());
                }
            }
        }

        removeCombustibles(atmoMap);
        atmosphericComposition.clear();

        atmosphericComposition.addAll(atmoMap.values());
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

    public static int getWaterVaporFactor(int baseTemperature, HydrosphereDescription hydrosphereDescription, int hydrosphere) {
        int waterVaporFactor;
        if (hydrosphereDescription.equals(HydrosphereDescription.LIQUID)
                || hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)) {
            waterVaporFactor = (int) Math.max(0, (baseTemperature - 240) / 100.0 * hydrosphere * (Dice._2d6() - 1));
        } else {
            waterVaporFactor = 0;
        }
        return waterVaporFactor;
    }

    public static void removeCombustibles(Map<String, AtmosphericGases> atmoMap) {
        int removedpercentages = 0;
        if (atmoMap.containsKey("CH4")) {
            removedpercentages += atmoMap.get("CH4").getPercentageInAtmo();
            atmoMap.remove("CH4");
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

    public static double findGreenhouseGases(Set<AtmosphericGases> atmoshericComposition,
                                             double atmoPressure,
                                             int baseTemperature,
                                             HydrosphereDescription hydrosphereDescription,
                                             int hydrosphere) {
        double tempGreenhouseGasEffect = 0;

        for (AtmosphericGases gas : atmoshericComposition) {

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

        return 1 + sqrt(atmoPressure) * 0.01 * (Dice._2d6() - 1)
                + sqrt(tempGreenhouseGasEffect) * 0.1
                + waterVaporFactor * 0.1;
    }

    public static TemperatureFacts.TemperatureFactsBuilder setAllKindOfLocalTemperature(double atmoPressure,
                                                                                        int hydrosphere,
                                                                                        double rotationalPeriod,
                                                                                        double axialTilt,
                                                                                        double surfaceTemp,
                                                                                        double orbitalPeriod) {

        double[][] temperatureRangeBand = new double[][]{ // First is Low Moderation atmos, then Average etc
                {1.10, 1.07, 1.05, 1.03, 1.00, 0.97, 0.93, 0.87, 0.78, 0.68},
                {1.05, 1.04, 1.03, 1.02, 1.00, 0.98, 0.95, 0.90, 0.82, 0.75},
                {1.02, 1.02, 1.02, 1.01, 1.00, 0.99, 0.98, 0.95, 0.91, 0.87},
                {1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00}
        };

        double[] summerTemperature = new double[10];
        double[] winterTemperature = new double[10];
        double[] latitudeTemperature = new double[10];
        double[] baseTemperature = new double[10];

        int testModeration = 0;
        testModeration += (hydrosphere - 60) / 10;
        testModeration += (atmoPressure < 0.1) ? -3 : 1;
        testModeration += (int) atmoPressure;
        testModeration += (rotationalPeriod < 10) ? -3 : 1;
        testModeration += (int) (Math.sqrt(rotationalPeriod / 24)); //Shouldn't this be negative?
        testModeration += (int) (10 / axialTilt);

        String atmoModeration;
        if (atmoPressure == 0) atmoModeration = "No";
        else if (atmoPressure > 10) atmoModeration = "Extreme";
        else if (testModeration < -2) atmoModeration = "Low";
        else if (testModeration > 2) atmoModeration = "High";
        else atmoModeration = "Average";

        int atmoIndex;
        switch (atmoModeration) {
            case "High":
                atmoIndex = 2;
                break;
            case "Average":
                atmoIndex = 1;
                break;
            case "Extreme":
                atmoIndex = 3;
                break;
            default:
                atmoIndex = 0;
                break;
        }

        for (int i = 0; i < 10; i++) {
            latitudeTemperature[i] = temperatureRangeBand[atmoIndex][i] * surfaceTemp;
        }

        for (int i = 0; i < 10; i++) {
            baseTemperature[i] = latitudeTemperature[i] - 274;
        }

        for (int i = 0; i < 10; i++) {

            double seasonEffect = 1;
            // This part is supposed to shift the rangebands for summer /winter effects, it makes an
            // (to me unproven) assumption that winter temperatures at the poles is not changed by seasonal effects
            // this feels odd but I have to delve further into the science before I dismiss it.
            // the effect occurs from the intersection of axial tilt effects and rangeband effects in a way that
            //makes me suspect it is unintentional.
            int axialTiltEffect = (int) (axialTilt / 10);
            int summer = Math.max(0, i - axialTiltEffect);
            int winter = Math.min(9, i + axialTiltEffect);

            if (i < 3 && axialTiltEffect < 4) seasonEffect *= 0.75;
            if (i > 8 && axialTiltEffect > 3) seasonEffect *= 2;
            if (orbitalPeriod < 0.25 && !atmoModeration.equals("Low")) seasonEffect *= 0.75;
            if (orbitalPeriod > 3 && !atmoModeration.equals("High") && axialTilt > 40) seasonEffect *= 1.5;

            summerTemperature[i] = (int) (latitudeTemperature[summer] - latitudeTemperature[i]) * seasonEffect;
            winterTemperature[i] = (int) (latitudeTemperature[winter] - latitudeTemperature[i]) * seasonEffect;
        }
        return TemperatureFacts.builder()
                               .rangeBandTemperature(DoubleStream.of(baseTemperature)
                                                                 .mapToInt(t -> (int) Math.ceil(t))
                                                                 .toArray())
                               .rangeBandTempSummer(DoubleStream.of(summerTemperature)
                                                                .mapToInt(t -> (int) Math.ceil(t))
                                                                .toArray())
                               .rangeBandTempWinter(DoubleStream.of(winterTemperature)
                                                                .mapToInt(t -> (int) Math.ceil(t))
                                                                .toArray());
    }

    public static void setDayNightTemp(TemperatureFacts.TemperatureFactsBuilder tempTempFacts, int baseTemperature, double luminosity, double orbitDistance, double atmoPressure, double rotationPeriod) {

        //Assymerical sigmoidal:  5-parameter logistic (5PL)
        var increasePerHourFactor = -1.554015 + (0.9854966 - -1.554015)/Math.pow(1 + Math.pow(atmoPressure/19056230d,0.5134927),1094.463);
        var maxDayIncreaseMultiple = 7.711577 + (0.2199364 - 7.711577)/Math.pow(1 + Math.pow(atmoPressure/2017503d,1.004679),757641.3);
        var incomingRadiation = luminosity/sqrt(orbitDistance);

        var daytimeMax = Math.min(incomingRadiation*increasePerHourFactor*rotationPeriod/2d,baseTemperature*incomingRadiation*maxDayIncreaseMultiple);

        var decresePerHour =  -0.5906138 + (19.28838 - -0.5906138)/Math.pow(1 + Math.pow(atmoPressure/291099200d,0.5804294),172207.2);
        var maxNigthDecreaseMultiple = 0.03501408 + (0.7690167 - 0.03501408)/Math.pow(1 + Math.pow(atmoPressure/6815738d,0.7782145),322006.2);

        var nighttimeMin =- Math.min(decresePerHour*rotationPeriod/2d,maxNigthDecreaseMultiple*baseTemperature);

        System.out.println("+"+daytimeMax+"/"+nighttimeMin);
        System.out.println("[+"+increasePerHourFactor+"/"+decresePerHour+"]");
        tempTempFacts.nightTempMod(BigDecimal.valueOf(nighttimeMin).round(NumberUtilities.TWO));
        tempTempFacts.dayTempMod(BigDecimal.valueOf(daytimeMax).round(NumberUtilities.TWO));

    }


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
        part[0] = (5 * (Dice._2d6()) + 33); //size of primary gas
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