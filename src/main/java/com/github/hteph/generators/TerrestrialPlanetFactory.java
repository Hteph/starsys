package com.github.hteph.generators;

import com.github.hteph.generators.utils.LifeMethods;
import com.github.hteph.generators.utils.MakeAtmosphere;
import com.github.hteph.generators.utils.PlanetaryUtils;
import com.github.hteph.generators.utils.TempertureMethods;
import com.github.hteph.repository.objects.AtmosphericGases;
import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.FindAtmoPressure;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.StreamUtilities;
import com.github.hteph.utils.enums.BaseElementOfLife;
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

import static com.github.hteph.generators.utils.MakeAtmosphere.checkHydrographics;
import static com.github.hteph.generators.utils.MakeAtmosphere.findGreenhouseGases;
import static com.github.hteph.generators.utils.PlanetaryUtils.findAlbedo;
import static com.github.hteph.generators.utils.PlanetaryUtils.findTectonicGroup;
import static com.github.hteph.generators.utils.PlanetaryUtils.getDensity;
import static com.github.hteph.generators.utils.PlanetaryUtils.getTectonicActivityGroup;
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
        Set<AtmosphericGases> atmoshericComposition;
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

        var magneticField = PlanetaryUtils.getMagneticField(rotationalPeriod,
                                                            tectonicCore,
                                                            tectonicActivityGroup,
                                                            star,
                                                            density,
                                                            mass
        );

        planetBuilder.tectonicCore(tectonicCore)
                     .tectonicActivityGroup(tectonicActivityGroup)
                     .magneticField(BigDecimal.valueOf(magneticField).round(TWO));

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
                                                               magneticField,
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
        if (!atmoshericComposition.isEmpty()) MakeAtmosphere.checkAtmo(atmoshericComposition, atmoPressure.doubleValue());
        planetBuilder.atmoPressure(atmoPressure.round(THREE));
        // The composition could be adjusted for the existence of life, so is set below

        albedo = findAlbedo(IS_INNER_ZONE, atmoPressure.doubleValue(), hydrosphereDescription, hydrosphere);
        planetBuilder.albedo(BigDecimal.valueOf(albedo).round(TWO));
        //adjusting base Temperature for albedo
        baseTemperature = (int)( baseTemperature*albedo);
        //Bioshpere

        hasGaia = LifeMethods.testLife(baseTemperature,
                                       atmoPressure.doubleValue(),
                                       hydrosphere,
                                       atmoshericComposition,
                                       star.getAge().doubleValue(),
                                       magneticField);

        if (hasGaia) lifeType = LifeMethods.findLifeType(atmoshericComposition, star.getAge().doubleValue());
        else lifeType = Breathing.NONE;

        if (lifeType.equals(Breathing.OXYGEN)) {
            MakeAtmosphere.adjustForOxygen(atmoPressure.doubleValue(), atmoshericComposition);
        }

        double greenhouseFactor = findGreenhouseGases(atmoshericComposition,
                                                      atmoPressure.doubleValue(),
                                                      baseTemperature,
                                                      hydrosphereDescription,
                                                      hydrosphere,
                                                      hasGaia);

        int surfaceTemp = TempertureMethods.getSurfaceTemp(baseTemperature,
                                                           atmoPressure,
                                                           greenhouseFactor,
                                                           hasGaia,
                                                           lifeType);

        if (!atmoshericComposition.isEmpty()) MakeAtmosphere.checkAtmo(atmoshericComposition, atmoPressure.doubleValue());
        planetBuilder.atmosphericComposition(atmoshericComposition);


        //TODO Legacy to be removed
        planetBuilder.lifeType(lifeType);


        //Climate -------------------------------------------------------
        // sets all the temperature stuff from axial tilt etc etc

        var temperatureFacts = TempertureMethods.setSeasonalTemperature(atmoPressure.doubleValue(),
                                                                        hydrosphere,
                                                                        rotationalPeriod,
                                                                        axialTilt,
                                                                        surfaceTemp,
                                                                        orbitalPeriod); // sets all the temperature stuff from axial tilt etc etc
        System.out.println("Stored surface temp (in C) = "+(surfaceTemp-274));
        temperatureFacts.surfaceTemp(surfaceTemp);

        if (lifeType != Breathing.NONE) {
            var biosphere = Biosphere.builder()
                                     .homeworld(name)
                                     .respiration(lifeType)
                                     .baseElement(surfaceTemp<360 ? BaseElementOfLife.CARBON : BaseElementOfLife.SILICA);
            planetBuilder.life(biosphere.build());
        }

        //TODO Weather and day night temp cycle
        TempertureMethods.setDayNightTemp(temperatureFacts,
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




    private static int getEccentryMod(char orbitalObjectClass) {
        int eccentryMod = 1;
        if (orbitalObjectClass == 'C' || orbitalObjectClass == 'c') eccentryMod += 3;
        return eccentryMod;
    }

    private static int getBaseSize(char orbitalObjectClass) {

        int baseSize = 900 + Dice.d10() * 10; //Default for planets
        if (orbitalObjectClass == 't' || orbitalObjectClass == 'c') baseSize = 90 + Dice.d10();
        return baseSize;
    }
}
