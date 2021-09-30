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
import com.github.hteph.repository.objects.wrappers.Homeworld;
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
import static com.github.hteph.utils.NumberUtilities.THREE;
import static com.github.hteph.utils.NumberUtilities.TWO;
import static com.github.hteph.utils.NumberUtilities.cubed;
import static com.github.hteph.utils.NumberUtilities.sqrt;
import static com.github.hteph.utils.NumberUtilities.squared;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class TerrestrialPlanetFactory {

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
        Set<AtmosphericGases> atmosphericComposition;

        double albedo;
        String tectonicActivityGroup;

        boolean hasGaia;
        Breathing lifeType = null;
        double tidelock;


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

        final int eccentryMod = getEccentryMod(orbitalObjectClass);
        double eccentricity = eccentryMod * (Dice._2d6() - 2) / (100.0 * Dice.d6());

        orbitalFacts.orbitalInclination(BigDecimal.valueOf(eccentryMod * (Dice._2d6()) / (1.0 + mass / 10.0))
                                                  .round(THREE));

        var tempMoonPlanet = planetBuilder.orbitalFacts(orbitalFacts.build()).build();

        List<Planet> moonList = GenerateMoonSystem.terrestialMoons(tempMoonPlanet, orbitDistance, IS_INNER_ZONE);
        planetBuilder.moonList(moonList);

        //Tidal forces
        final double sumOfLunarTidal = StreamUtilities.getStreamEmptyIfNull(moonList)
                                                      .map(m -> m.getLunarTidal().doubleValue())
                                                      .reduce(0d, Double::sum);

        boolean tidelocked = false;


        double tidalForce = (star.getMass().doubleValue() * 26640000 / cubed(orbitDistance.doubleValue() * 400.0))
                / (1.0 + sumOfLunarTidal / 10);
        tidelock = (0.83 + (Dice._2d6() - 2) * 0.03) * tidalForce * star.getAge().doubleValue() / 6.6;
        if (tidelock > 1) {
            tidelocked = true;
            //TODO Tidelocked planets generally can't have moon, but caught objects should be allowed?
            planetBuilder.moonList(Collections.emptyList());
        }
        planetBuilder.tideLockedStar(tidelocked);

        //Axuíal tilt
        double tiltDivisor = tidelocked ? 10d : 2d;
        final double axialTilt = (int) (10 * Dice._3d6() / tiltDivisor * Math.random());
        planetBuilder.axialTilt(BigDecimal.valueOf(axialTilt).round(TWO));

        //Rotation - day/night cycle
        if (tidelocked) {
            rotationalPeriod = orbitalPeriod * 365 * 24;
        } else {
            double tidalEffects = (1 + 0.2 * Math.max(0, (tidalForce * star.getAge().doubleValue() - sqrt(mass))));
            rotationalPeriod = (Dice._3d6() + 12 + Dice.d10() / 10d) * tidalEffects;


            if (Dice.d10() < 2) {
                rotationalPeriod = Math.pow(rotationalPeriod, Dice.d3() + Dice.d10() / 10.0);
            }

            final double ORBITAL_PERIOD_HOURS = orbitalPeriod * 365 * 24;

            if (rotationalPeriod > (ORBITAL_PERIOD_HOURS) / 2.0) {
                planetBuilder.resonanceOrbitalPeriod(true);
                //TODO update this to TableMaker
                double[] resonanceArray = {0.5, 2 / 3.0, 1, 1.5, 2, 2.5, 3, 3.5};
                double[] eccentricityEffect = {0.1, 0.15, 0.21, 0.39, 0.57, 0.72, 0.87, 0.87};

                int resultResonance = Arrays.binarySearch(resonanceArray, rotationalPeriod / ORBITAL_PERIOD_HOURS);

                if (resultResonance < 0) {
                    eccentricity = eccentricityEffect[-resultResonance - 2];
                    rotationalPeriod = resonanceArray[-resultResonance - 2] * ORBITAL_PERIOD_HOURS;
                } else {
                    resultResonance = Math.min(resultResonance, 6); //TODO there is something fishy here, Edge case of greater than
                    // 0.87 relation still causes problem Should rethink methodology
                    eccentricity = eccentricityEffect[resultResonance];
                    rotationalPeriod = resonanceArray[-resultResonance] * ORBITAL_PERIOD_HOURS;
                }

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
        baseTemperature = TempertureMethods.findBaseTemp(orbitDistance.doubleValue(), star.getLuminosity()
                                                                                          .doubleValue());

        //base temp is an value of little use beyond this generator and is not propagated to the planet object

        //Hydrosphere
        hydrosphereDescription = MakeAtmosphere.findHydrosphereDescription(IS_INNER_ZONE, baseTemperature);
        hydrosphere = MakeAtmosphere.findTheHydrosphere(hydrosphereDescription, planetRadius);

        planetBuilder.hydrosphereDescription(hydrosphereDescription);
        planetBuilder.hydrosphere(hydrosphere);

        //Atmoshperic details
        atmosphericComposition = MakeAtmosphere.createPlanetary(star,
                                                                baseTemperature,
                                                                tectonicActivityGroup,
                                                                planetRadius,
                                                                gravity,
                                                                magneticField,
                                                                planetBuilder);
        var tempPlanet = planetBuilder.build();

        double atmoPressure = FindAtmoPressure.calculate(tectonicActivityGroup,
                                                         hydrosphere,
                                                         tempPlanet.isBoilingAtmo(),
                                                         mass,
                                                         atmosphericComposition);

        // TODO Special considerations for c objects, this should be expanded upon when these gets more details

        if (orbitalObjectClass == 'c') { //These should never had a chance to get an "real" atmosphere in the first
            // place but may have some traces
            if (Dice.d6(6)) atmoPressure = 0;
            else atmoPressure = 0.001;
        }

        if (atmoPressure == 0) atmosphericComposition.clear();
        if (atmosphericComposition.isEmpty()) { //There are edge cases where all of atmo has boiled away
            atmoPressure = 0;
            if (hydrosphereDescription == HydrosphereDescription.LIQUID && hydrosphere > 0) {
                planetBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
                planetBuilder.hydrosphere(0);
            }

        }
        if (!atmosphericComposition.isEmpty())
            MakeAtmosphere.checkAtmo(atmosphericComposition, atmoPressure);
        // The composition could be adjusted for the existence of life, so is set below

        albedo = findAlbedo(IS_INNER_ZONE, atmoPressure, hydrosphereDescription, hydrosphere);
        planetBuilder.albedo(BigDecimal.valueOf(albedo).round(TWO));
        //adjusting base Temperature for albedo
        baseTemperature = (int) (baseTemperature * albedo);
        //Bioshpere
        double greenhouseFactor = findGreenhouseGases(atmosphericComposition,
                                                      atmoPressure,
                                                      baseTemperature,
                                                      hydrosphereDescription,
                                                      hydrosphere);

        int surfaceTemp = TempertureMethods.getSurfaceTemp(baseTemperature,
                                                           atmoPressure,
                                                           greenhouseFactor);

        hasGaia = LifeMethods.testLife(surfaceTemp,
                                       atmoPressure,
                                       hydrosphere,
                                       atmosphericComposition,
                                       star.getAge().doubleValue(),
                                       magneticField, tectonicActivityGroup);


        var biosphere = Biosphere.builder();
        if (hasGaia) {
            lifeType = LifeMethods.findLifeType(atmosphericComposition, star.getAge().doubleValue());
            if (lifeType.equals(Breathing.OXYGEN)) {
                int oxygen = MakeAtmosphere.adjustForOxygen(atmoPressure, atmosphericComposition);
                atmoPressure *= 1 + oxygen / 100d; //completly invented buff for atmopressure of oxygen breathers
            }


            biosphere.respiration(lifeType)
                     .baseElement(surfaceTemp < 360 + Dice.d20() ? BaseElementOfLife.CARBON : BaseElementOfLife.SILICA);
        }
        if (!atmosphericComposition.isEmpty()) MakeAtmosphere.checkAtmo(atmosphericComposition, atmoPressure);

        planetBuilder.atmosphericComposition(atmosphericComposition);
        planetBuilder.atmoPressure(BigDecimal.valueOf(atmoPressure).round(THREE));

        //Climate -------------------------------------------------------
        // sets all the temperature stuff from axial tilt etc etc

        var temperatureFacts = TempertureMethods.setSeasonalTemperature(atmoPressure,
                                                                        hydrosphere,
                                                                        rotationalPeriod,
                                                                        axialTilt,
                                                                        surfaceTemp,
                                                                        orbitalPeriod); // sets all the temperature stuff from axial tilt etc etc

        temperatureFacts.surfaceTemp(surfaceTemp);
        temperatureFacts.eccentricityVariation(TempertureMethods.setExcentricityVariation(eccentricity,
                                                                                          orbitDistance.doubleValue(),
                                                                                          star.getLuminosity()
                                                                                              .doubleValue()));


        //TODO Weather and day night temp cycle
        TempertureMethods.setDayNightTemp(temperatureFacts,
                                          baseTemperature,
                                          star.getLuminosity().doubleValue(),
                                          orbitDistance.doubleValue(),
                                          atmoPressure,
                                          rotationalPeriod);


//Sanity check of water
        checkHydrographics(hydrosphereDescription,
                           hydrosphere,
                           atmoPressure,
                           planetBuilder,
                           surfaceTemp,
                           temperatureFacts.build().getRangeBandTempWinter(),
                           temperatureFacts.build().getRangeBandTempSummer());


        var homeworld = Homeworld.builder();
        homeworld.hydrosphereDescription(hydrosphereDescription)
                 .name(name)
                 .stellarObjectType(StellarObjectType.TERRESTRIAL)
                 .temperatureFacts(temperatureFacts.build())
                 .gravity(gravity)
                 .magneticField(magneticField);


        planetBuilder.orbitalFacts(orbitalFacts.build());
        planetBuilder.temperatureFacts(temperatureFacts.build());

        if (hasGaia) planetBuilder.life(biosphere.homeworld(homeworld.build()).build());
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
