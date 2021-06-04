package com.github.hteph.generators;

import com.github.hteph.generators.utils.LifeMethods;
import com.github.hteph.generators.utils.MakeAtmosphere;
import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.FindAtmoPressure;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.tables.TectonicActivityTable;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.StreamUtilities;
import com.github.hteph.utils.enums.Breathing;
import com.github.hteph.utils.enums.HydrosphereDescription;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import static com.github.hteph.generators.utils.MakeAtmosphere.checkHydrographics;
import static com.github.hteph.generators.utils.MakeAtmosphere.findGreenhouseGases;
import static com.github.hteph.utils.NumberUtilities.cubed;
import static com.github.hteph.utils.NumberUtilities.sqrt;
import static com.github.hteph.utils.NumberUtilities.squared;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class TerrestrialPlanetFactory {

    static final MathContext TWO = new MathContext(2);
    static final MathContext THREE = new MathContext(3);
    static final MathContext FOUR = new MathContext(4);
    static final MathContext FIVE = new MathContext(5);

    public static Planet generate(final String archiveID,
                                  final String name,
                                  final String description,
                                  final String classificationName,
                                  BigDecimal orbitDistance,
                                  final char orbitalObjectClass,
                                  final Star star,
                                  final double lunarOrbitDistance) {

        //Last parameter (lunarOrbitDistance) is only used if this is a moon to be created

        double orbitalPeriod; //in earth years
        double rotationalPeriod; // in hours

        String tectonicCore;

        int baseTemperature;
        HydrosphereDescription hydrosphereDescription;
        int hydrosphere;
        TreeSet<AtmosphericGases> atmoshericComposition;
        BigDecimal atmoPressure;
        double albedo;
        String tectonicActivityGroup;

        boolean hasGaia;
        Breathing lifeType;
        double tidelock;

        System.out.println(name);
        var planetBuilder = Planet.builder()
                                  .archiveID(archiveID)
                                  .name(name)
                                  .stellarObjectType(StellarObjectType.TERRESTRIAL)
                                  .description(description)
                                  .classificationName(classificationName);

        var orbitalFacts = OrbitalFacts.builder();
        orbitalFacts.orbitsAround(star);
        orbitalFacts.orbitalDistance(orbitDistance.round(THREE));

        final double SNOWLINE = 5 * sqrt(star.getLuminosity().doubleValue());
        final boolean IS_INNER_ZONE = orbitDistance.doubleValue() < SNOWLINE;

        // size may not be all, but here it is set
        //TODO add greater varity for moon objects, depending on planet
        final int planetRadius = Dice._2d6() * getBaseSize(orbitalObjectClass);
        planetBuilder.radius(planetRadius);

        //density
        final double density = getDensity(orbitDistance, star, SNOWLINE);
        final double mass = cubed(planetRadius / 6380.0) * density;
        final double gravity = mass / squared((planetRadius / 6380.0));

        orbitalPeriod = sqrt(cubed(orbitDistance.doubleValue()) / star.getMass().doubleValue()); //in earth days
        orbitalFacts.orbitalPeriod(BigDecimal.valueOf(orbitalPeriod).round(THREE));

        planetBuilder.mass(BigDecimal.valueOf(mass).round(THREE))
                     .gravity(BigDecimal.valueOf(gravity).round(TWO))
                     .density(BigDecimal.valueOf(density).round(TWO));

        //Eccentricity and Inclination

        final double axialTilt = (int) (10 * Dice._3d6() / 2.0 * Math.random());
        final int eccentryMod = getEccentryMod(orbitalObjectClass);
        double eccentricity = eccentryMod * (Dice._2d6() - 2) / (100.0 * Dice.d6());

        planetBuilder.axialTilt(BigDecimal.valueOf(axialTilt).round(THREE));
        orbitalFacts.orbitalInclination(BigDecimal.valueOf(eccentryMod * (Dice._2d6()) / (1.0 + mass / 10.0))
                                                  .round(THREE));

        // TODO tidelocked or not should take into consideration moons too!
        var tempMoonPlanet = planetBuilder.orbitalFacts(orbitalFacts.build()).build();

        List<Planet> moonList = GenerateMoonSystem.terrestialMoons(tempMoonPlanet, orbitDistance, IS_INNER_ZONE);
        planetBuilder.moonList(moonList);


        final double sumOfLunarTidal = StreamUtilities.getStreamEmptyIfNull(moonList)
                                                      .map(m -> m.getLunarTidal().doubleValue())
                                                      .reduce(0d, Double::sum);

        boolean tidelocked = false;

        double tidalForce = (star.getMass().doubleValue() * 26640000 / cubed(orbitDistance.doubleValue() * 400.0))
                / (1.0 + sumOfLunarTidal);
        tidelock = (0.83 + (Dice._2d6() - 2) * 0.03) * tidalForce * star.getAge().doubleValue() / 6.6;
        if (tidelock > 1) {
            tidelocked = true;
            //TODO Tidelocked planets generally can't have moon, but caught objects should be allowed?
            planetBuilder.moonList(Collections.emptyList());
        }

        planetBuilder.tideLockedStar(tidelocked);

        //Rotation - day/night cycle
        if (tidelocked) {
            rotationalPeriod = orbitalPeriod * 365 * 24;
        } else {
            double tidalEffects = (1 + 0.1 * Math.max(0, (tidalForce * star.getAge().doubleValue() - sqrt(mass))));
            rotationalPeriod = (Dice._3d6() + 12 + Dice.d10() / 10d) * tidalEffects;
            System.out.println("start rotation: " + rotationalPeriod);
            System.out.println("tidal effects: " + tidalEffects);


            if (Dice.d10() < 2) {
                System.out.println("deviant rotation: " + rotationalPeriod);
                rotationalPeriod = Math.pow(rotationalPeriod, Dice.d3() + Dice.d10() / 10.0);
                System.out.println("Becomes: " + rotationalPeriod);

            }

            final double ORBITAL_PERIOD_HOURS = orbitalPeriod * 365 * 24;

            if (rotationalPeriod > (ORBITAL_PERIOD_HOURS) / 2.0) {
                planetBuilder.resonanceOrbitalPeriod(true);
                //TODO update this to TableMaker
                System.out.println("start rotation: " + rotationalPeriod);
                double[] resonanceArray = {0.5, 2 / 3.0, 1, 1.5, 2, 2.5, 3, 3.5};
                double[] eccentricityEffect = {0.1, 0.15, 0.21, 0.39, 0.57, 0.72, 0.87, 0.87};

                int resultResonance = Arrays.binarySearch(resonanceArray, rotationalPeriod / ORBITAL_PERIOD_HOURS);

                System.out.println("resonance: " + resultResonance);
                if (resultResonance < 0) {
                    eccentricity = eccentricityEffect[-resultResonance - 2];
                    rotationalPeriod = resonanceArray[-resultResonance - 2] * ORBITAL_PERIOD_HOURS;
                } else {
                    resultResonance = Math.min(resultResonance, 6); //TODO there is something fishy here, Edge case of greater than
                    // 0.87 relation still causes problem Should rethink methodology
                    eccentricity = eccentricityEffect[resultResonance];
                    rotationalPeriod = resonanceArray[-resultResonance] * ORBITAL_PERIOD_HOURS;
                }

                System.out.println("reulting rotation: " + rotationalPeriod);
            }
        }

        orbitalFacts.orbitalEccentricity(BigDecimal.valueOf(eccentricity).round(TWO));
        planetBuilder.rotationalPeriod(BigDecimal.valueOf(rotationalPeriod).round(THREE));

        //TODO tectonics should include moons!
        //TODO c type should have special treatment

        tectonicCore = findTectonicGroup(IS_INNER_ZONE, density);
        tectonicActivityGroup = getTectonicActivityGroup(star, tidalForce, mass);

        planetBuilder.tectonicCore(tectonicCore)
                     .tectonicActivityGroup(tectonicActivityGroup)
                     .magneticField(BigDecimal.valueOf(getMagneticField(rotationalPeriod,
                                                                        tectonicCore,
                                                                        tectonicActivityGroup,
                                                                        star,
                                                                        density,
                                                                        mass
                     )).round(TWO));

        //Temperature
        baseTemperature = (int) (255 / sqrt((orbitDistance.doubleValue()
                / sqrt(star.getLuminosity().doubleValue()))));

        //base temp is an value of little use beyond this generator and is not propagated to the planet object

        //Hydrosphere
        hydrosphereDescription = MakeAtmosphere.findHydrosphereDescription(IS_INNER_ZONE, baseTemperature);
        hydrosphere = MakeAtmosphere.findTheHydrosphere(hydrosphereDescription, planetRadius);

        planetBuilder.hydrosphereDescription(hydrosphereDescription);
        planetBuilder.hydrosphere(hydrosphere);

        //Atmoshperic details
        atmoshericComposition = MakeAtmosphere.createPlanetary(star,
                                                               baseTemperature,
                                                               tectonicActivityGroup,
                                                               planetRadius,
                                                               gravity,
                                                               planetBuilder);
        var tempPlanet = planetBuilder.build();

        atmoPressure = FindAtmoPressure.calculate(tectonicActivityGroup,
                                                  hydrosphere,
                                                  tempPlanet.isBoilingAtmo(),
                                                  mass,
                                                  atmoshericComposition);

        // TODO Special considerations for c objects, this should be expanded upon when these gets more details

        if (orbitalObjectClass == 'c') { //These should never had a chance to get an "real" atmosphere in the first
            // place but may have some traces
            if (Dice.d6(6)) atmoPressure = BigDecimal.valueOf(0);
            else atmoPressure = BigDecimal.valueOf(0.001);
        }

        if (atmoPressure.doubleValue() == 0) atmoshericComposition.clear();
        if (atmoshericComposition.isEmpty()) { //There are edge cases where all of atmo has boiled away
            atmoPressure = BigDecimal.valueOf(0);
            if (hydrosphereDescription == HydrosphereDescription.LIQUID && hydrosphere > 0) {
                planetBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
                planetBuilder.hydrosphere(0);
            }

        }

        planetBuilder.atmoPressure(atmoPressure.round(THREE));
        // The composition could be adjusted for the existence of life, so is set below

        //Bioshpere

        hasGaia = LifeMethods.testLife(baseTemperature,
                                       atmoPressure.doubleValue(),
                                       hydrosphere,
                                       atmoshericComposition,
                                       star.getAge().doubleValue());

        if (hasGaia) lifeType = LifeMethods.findLifeType(atmoshericComposition, star.getAge().doubleValue());
        else lifeType = Breathing.NONE;

        if (lifeType.equals(Breathing.OXYGEN))
            MakeAtmosphere.adjustForOxygen(atmoPressure.doubleValue(), atmoshericComposition);

        albedo = findAlbedo(IS_INNER_ZONE, atmoPressure.doubleValue(), hydrosphereDescription, hydrosphere);
        planetBuilder.albedo(BigDecimal.valueOf(albedo).round(TWO));
        double greenhouseFactor = findGreenhouseGases(atmoshericComposition,
                                                      atmoPressure.doubleValue(),
                                                      baseTemperature,
                                                      hydrosphereDescription,
                                                      hydrosphere);

        //TODO Here adding some Gaia moderation factor (needs tweaking probably) moving a bit more towards
        // water/carbon ideal
        if (lifeType.equals(Breathing.OXYGEN) && baseTemperature > 350) greenhouseFactor *= 0.8;
        if (lifeType.equals(Breathing.OXYGEN) && baseTemperature < 250) greenhouseFactor *= 1.2;

        int surfaceTemp = getSurfaceTemp(baseTemperature, atmoPressure, albedo, greenhouseFactor, hasGaia);

        if (!atmoshericComposition.isEmpty()) MakeAtmosphere.checkAtmo(atmoshericComposition);
        planetBuilder.atmosphericComposition(atmoshericComposition);

        if (lifeType != Breathing.NONE) {
            var biosphere = Biosphere.builder().respiration(lifeType);
            planetBuilder.life(biosphere.build());
        }
        //TODO Legacy to be removed
        planetBuilder.lifeType(lifeType);


        //Climate -------------------------------------------------------
        // sets all the temperature stuff from axial tilt etc etc

        var temperatureFacts = MakeAtmosphere.setAllKindOfLocalTemperature(atmoPressure.doubleValue(),
                                                                           hydrosphere,
                                                                           rotationalPeriod,
                                                                           axialTilt,
                                                                           surfaceTemp,
                                                                           orbitalPeriod); // sets all the temperature stuff from axial tilt etc etc
        temperatureFacts.surfaceTemp(surfaceTemp);
        //TODO Weather and day night temp cycle
        MakeAtmosphere.setDayNightTemp(temperatureFacts,
                                       baseTemperature,
                                       star.getLuminosity().doubleValue(),
                                       orbitDistance.doubleValue(),
                                       atmoPressure.doubleValue(),
                                       rotationalPeriod);


//Sanity check of water
        checkHydrographics(hydrosphereDescription,
                           hydrosphere,
                           atmoPressure,
                           planetBuilder,
                           surfaceTemp,
                           temperatureFacts.build().getRangeBandTempWinter(),
                           temperatureFacts.build().getRangeBandTempSummer());

        planetBuilder.orbitalFacts(orbitalFacts.build());
        planetBuilder.temperatureFacts(temperatureFacts.build());
        return planetBuilder.build();
    }


    private static double getMagneticField(double rotationalPeriod, String tectonicCore, String tectonicActivityGroup, Star orbitingAround, double density, double mass) {
        double magneticField;
        if (tectonicCore.contains("metal")) {
            magneticField = 10 / (sqrt((rotationalPeriod / 24.0)))
                    * squared(density)
                    * sqrt(mass)
                    / orbitingAround.getAge().doubleValue();
            if (tectonicCore.contains("small")) magneticField *= 0.5;
            if (tectonicCore.contains("medium")) magneticField *= 0.75;
            if (tectonicActivityGroup.equals("Dead")) magneticField = Dice.d6() / 15.0;
        } else {
            magneticField = Dice.d6() / 20.0;
        }
        return magneticField;
    }

    private static String getTectonicActivityGroup(Star orbitingAround, double tidalForce, double mass) {
        double tectonicActivity;
        String tectonicActivityGroup;
        tectonicActivity = (5 + Dice._2d6() - 2) * Math.pow(mass, 0.5) / orbitingAround.getAge().doubleValue();
        tectonicActivity *= (1 + 0.5 * tidalForce);
        tectonicActivityGroup = TectonicActivityTable.findTectonicActivityGroup(tectonicActivity);
        return tectonicActivityGroup;
    }

    private static int getEccentryMod(char orbitalObjectClass) {
        int eccentryMod = 1;
        if (orbitalObjectClass == 'C' || orbitalObjectClass == 'c') eccentryMod += 3;
        return eccentryMod;
    }

    private static double getDensity(BigDecimal orbitDistance, Star orbitingAround, double snowLine) {
        double density;
        if (orbitDistance.doubleValue() < snowLine) {
            density = 0.3 + (Dice._2d6() - 2) * 0.127 / Math.pow(
                    0.4 + (orbitDistance.doubleValue() / sqrt(orbitingAround.getLuminosity().doubleValue())), 0.67);
        } else {
            density = 0.3 + (Dice._2d6() - 2) * 0.05;
        }
        return density;
    }

    private static int getBaseSize(char orbitalObjectClass) {

        int baseSize = 900 + Dice.d10() * 10; //Default for planets
        if (orbitalObjectClass == 't' || orbitalObjectClass == 'c') baseSize = 90 + Dice.d10();
        return baseSize;
    }

    private static int getSurfaceTemp(int baseTemperature,
                                      BigDecimal atmoPressure,
                                      double albedo,
                                      double greenhouseFactor,
                                      boolean hasGaia) {

        // My take on the effect of greenhouse and albedo on temperature max planerary temp is 1000 and the half
        // point is 400
        double surfaceTemp;
        System.out.println("baseTemp before Greenhouse("+greenhouseFactor+" and albedo"+albedo+")= "+baseTemperature);
        if (hasGaia) {
            surfaceTemp = (baseTemperature * Math.pow((albedo), 1/4d) * sqrt(1+greenhouseFactor));;// 450d * (baseTemperature * albedo * greenhouseFactor)
                    // (400d + baseTemperature * albedo * greenhouseFactor);
        } else if (atmoPressure.doubleValue() > 0) {
            surfaceTemp = 800d * (baseTemperature * albedo * greenhouseFactor)
                    / (400d + baseTemperature * albedo * greenhouseFactor);
        } else {
            surfaceTemp = 1200d * (baseTemperature * albedo * greenhouseFactor)
                    / (800d + baseTemperature * albedo * greenhouseFactor);
        }

        System.out.println("baseTemp after Greenhouse= "+surfaceTemp);
        int altTemp = (int) (baseTemperature * Math.pow((albedo), 1/4d) * sqrt(1+greenhouseFactor));
        System.out.println("Alternative calc= "+altTemp);

        int altTemp2 = (int) (baseTemperature * albedo * greenhouseFactor);
        System.out.println("Alternative2 calc= "+altTemp2);

        return (int) surfaceTemp;
    }

    /*TODO
     * This should be reworked (in conjuction with atmo) to remove CL and F from naturally occuring and instead
     * treat them similar to Oxygen. Also the Ammonia is dependent on free water as written right now
     */


    private static double findAlbedo(boolean InnerZone,
                                     double atmoPressure,
                                     HydrosphereDescription hydrosphereDescription,
                                     int hydrosphere) {

        int mod = 0;
        int[] randAlbedoArray;
        Double[] albedoBase = new Double[]{0.75, 0.85, 0.95, 1.05, 1.15};

        if (InnerZone) {
            randAlbedoArray = new int[]{0, 2, 4, 7, 10};

            if (atmoPressure < 0.05) mod = 2;
            if (atmoPressure > 50) {
                mod = -4;
            } else if (atmoPressure > 5) {
                mod = -2;
            }
            if (hydrosphere > 50
                    && hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)
                    && mod > -2)
                mod = -2;
            if (hydrosphere > 90
                    && hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)
                    && mod > -4)
                mod = -4;

        } else {
            randAlbedoArray = new int[]{0, 4, 6, 8, 10};
            if (atmoPressure > 1) mod = 1;
        }
        return TableMaker.makeRoll(Dice._2d6() + mod, randAlbedoArray, albedoBase) + (Dice.d10() - 1) * 0.01;
    }

    private static int findTheHydrosphere(HydrosphereDescription hydrosphereDescription, int radius) {

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

    private static HydrosphereDescription findHydrosphereDescription(boolean InnerZone, int baseTemperature) {


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

    private static String findTectonicGroup(boolean InnerZone, double density) {

        String tempTectonics;
        if (InnerZone) {
            if (density < 0.7) {
                if (Dice.d6() < 4) {
                    tempTectonics = "Silicates core";
                } else {
                    tempTectonics = "Silicates, small metal core";
                }
            } else if (density < 1) {
                tempTectonics = "Iron-nickel, medium metal core";
            } else {
                tempTectonics = "Iron-nickel, large metal core";
            }
        } else {
            if (density < 0.3) {
                tempTectonics = "Ice core";
            } else if (density < 1) {
                tempTectonics = "Silicate core";
            } else {
                if (Dice.d6() < 4) {
                    tempTectonics = "Silicates core";
                } else {
                    tempTectonics = "Silicates, small metal core";
                }
            }
        }
        return tempTectonics;
    }
}
