package com.github.hteph.generators;


import com.github.hteph.generators.utils.LifeMethods;
import com.github.hteph.generators.utils.MakeAtmosphere;
import com.github.hteph.generators.utils.PlanetaryUtils;
import com.github.hteph.generators.utils.TempertureMethods;

import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.FindAtmoPressure;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.BaseElementOfLife;
import com.github.hteph.utils.enums.Breathing;
import com.github.hteph.utils.enums.HydrosphereDescription;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;

import static com.github.hteph.generators.utils.MakeAtmosphere.adjustForOxygen;
import static com.github.hteph.generators.utils.MakeAtmosphere.checkAtmo;
import static com.github.hteph.generators.utils.MakeAtmosphere.checkHydrographics;
import static com.github.hteph.generators.utils.MakeAtmosphere.findHydrosphereDescription;
import static com.github.hteph.generators.utils.MakeAtmosphere.findTheHydrosphere;
import static com.github.hteph.generators.utils.PlanetaryUtils.findAlbedo;
import static com.github.hteph.generators.utils.PlanetaryUtils.findTectonicGroup;
import static com.github.hteph.generators.utils.PlanetaryUtils.getDensity;
import static com.github.hteph.generators.utils.PlanetaryUtils.getTectonicActivityGroup;
import static com.github.hteph.utils.NumberUtilities.cubed;
import static com.github.hteph.utils.NumberUtilities.sqrt;
import static com.github.hteph.utils.NumberUtilities.squared;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class MoonFactory {

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
                                  final Planet orbitingAroundPlanet,
                                  final double lunarOrbitNumberInPlanetRadii) {


        boolean planetLocked = false;
        String tectonicCore;
        int baseTemperature;
        HydrosphereDescription hydrosphereDescription;
        int hydrosphere;

        String tectonicActivityGroup;

        double moonsPlanetMass = 0;
        double lunarOrbitalPeriod;

        double moonsPlanetsRadii = 0;

        Breathing lifeType;
        double tidalForce = 0;

        System.out.println(name);
        var moonBuilder = Planet.builder()
                                .archiveID(archiveID)
                                .name(name)
                                .stellarObjectType(StellarObjectType.MOON)
                                .description(description)
                                .classificationName(classificationName)
                                .lunarOrbitDistance(BigDecimal.valueOf(lunarOrbitNumberInPlanetRadii).round(THREE));

        var lumo = ((Star) (orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround())).getLuminosity();

        var orbitalFacts = OrbitalFacts.builder()
                                       .orbitsAround(orbitingAroundPlanet)
                                       .orbitalPeriod(orbitingAroundPlanet.getOrbitalFacts().getOrbitalPeriod());

        moonsPlanetMass = orbitingAroundPlanet.getMass().doubleValue();
        moonsPlanetsRadii = orbitingAroundPlanet.getRadius();

        //TODO sort that equation out
        var moonTidal = (moonsPlanetMass * 26640000 / 333000.0 / Math.pow(moonsPlanetsRadii * lunarOrbitNumberInPlanetRadii * 400 / 149600000, 3));
        moonBuilder.lunarTidal(BigDecimal.valueOf(moonTidal).round(THREE));

        var centralStar = (Star) orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround();

        final double SNOWLINE = 5 * sqrt(centralStar.getLuminosity().doubleValue());
        final boolean IS_INNER_ZONE = orbitDistance.doubleValue() < SNOWLINE;

        // size may not be all, but here it is set
        //TODO add greater varity for moon objects, depending on planet
        final int moonRadius = Math.min(orbitingAroundPlanet.getRadius() / 3,
                                        Dice._2d6() * getBaseSize(orbitalObjectClass));
        moonBuilder.radius(moonRadius);

        //density
        final double density = getDensity(orbitDistance, centralStar, SNOWLINE);
        final double mass = cubed(moonRadius / 6380.0) * density;
        final double gravity = mass / squared((moonRadius / 6380.0));

        //I assume this is in Earth days?
        lunarOrbitalPeriod = sqrt(cubed((lunarOrbitNumberInPlanetRadii * moonsPlanetsRadii) / 400000)
                                          * 793.64 / (moonsPlanetMass + mass));

        moonBuilder.mass(BigDecimal.valueOf(mass).round(THREE))
                   .gravity(BigDecimal.valueOf(gravity).round(TWO))
                   .density(BigDecimal.valueOf(density).round(TWO))
                   .lunarOrbitalPeriod(BigDecimal.valueOf(lunarOrbitalPeriod).round(THREE));

        //Eccentricity and Inclination TODO Are these actually Ok for moons?

        final double axialTilt = (int) (5 * Dice._3d6() / 2.0 * Math.random());
        final int eccentryMod = getEccentryMod(orbitalObjectClass);
        double eccentricity = eccentryMod * (Dice._2d6() - 2) / (100.0 * Dice.d6());

        moonBuilder.axialTilt(BigDecimal.valueOf(axialTilt).round(THREE));
        orbitalFacts.orbitalInclination(BigDecimal.valueOf(eccentryMod * (Dice._2d6()) / (1.0 + mass / 10.0))
                                                  .round(THREE));

        if (moonTidal > 1) planetLocked = true;
        moonBuilder.planetLocked(planetLocked);

        double rotationalPeriod;
        //Rotation - day/night cycle
        if (planetLocked) {
            rotationalPeriod = lunarOrbitalPeriod * 24;
        } else {
            rotationalPeriod = (Dice.d6() + Dice.d6() + 8)
                    * (1 + 0.1 * (tidalForce * centralStar.getAge().doubleValue() - sqrt(mass)));

            if (Dice.d6(2)) rotationalPeriod = Math.pow(rotationalPeriod, Dice.d6());

            if (rotationalPeriod > lunarOrbitalPeriod / 2.0) {

                double[] resonanceArray = {0.5, 2 / 3.0, 1, 1.5, 2, 2.5, 3, 3.5};
                double[] eccentricityEffect = {0.1, 0.15, 0.21, 0.39, 0.57, 0.72, 0.87, 0.87};

                int resultResonance = Arrays.binarySearch(resonanceArray, rotationalPeriod / lunarOrbitalPeriod);

                if (resultResonance < 0) {
                    eccentricity = eccentricityEffect[-resultResonance - 2];
                    rotationalPeriod = resonanceArray[-resultResonance - 2];
                } else {
                    resultResonance = Math.min(resultResonance, 6); //TODO there is something fishy here, Edge case of greater than
                    // 0.87 relation still causes problem Should rethink methodology
                    eccentricity = eccentricityEffect[resultResonance];
                    rotationalPeriod = resonanceArray[-resultResonance];
                }
            }
        }

        orbitalFacts.orbitalEccentricity(BigDecimal.valueOf(eccentricity));
        moonBuilder.rotationalPeriod(BigDecimal.valueOf(rotationalPeriod).round(THREE));

        //TODO tectonics should include moons!
        //TODO c type should have special treatment

        tectonicCore = findTectonicGroup(IS_INNER_ZONE, density);
        tectonicActivityGroup = getTectonicActivityGroup(centralStar, tidalForce, mass);

        var magneticField = PlanetaryUtils.getMagneticField(rotationalPeriod,
                                                            tectonicCore,
                                                            tectonicActivityGroup,
                                                            centralStar,
                                                            density,
                                                            mass);

        moonBuilder.tectonicCore(tectonicCore)
                   .tectonicActivityGroup(tectonicActivityGroup)
                   .magneticField(BigDecimal.valueOf(magneticField).round(TWO));

        //adding magnetic field from planet (where it exists i.e Jovians) for the forthcoming calculations
        if(orbitingAroundPlanet.getMagneticField() != null ) magneticField +=orbitingAroundPlanet.getMagneticField().doubleValue();
        //Temperature

        //base temp is an value of little use beyond this generator and is not propagated to the planet object
        baseTemperature = TempertureMethods.findBaseTemp(orbitDistance.doubleValue(), lumo.doubleValue());

        //Hydrosphere
        hydrosphereDescription = findHydrosphereDescription(IS_INNER_ZONE, baseTemperature);
        hydrosphere = findTheHydrosphere(hydrosphereDescription, moonRadius);

        moonBuilder.hydrosphereDescription(hydrosphereDescription);
        moonBuilder.hydrosphere(hydrosphere);


        //Atmospheric details
        var atmosphericComposition = MakeAtmosphere.createPlanetary(centralStar,
                                                               baseTemperature,
                                                               tectonicActivityGroup,
                                                               moonRadius,
                                                               gravity,
                                                               magneticField,
                                                               moonBuilder);
        var tempMoon = moonBuilder.build();

        double atmoPressure = FindAtmoPressure.calculate(tectonicActivityGroup,
                                                  hydrosphere,
                                                  tempMoon.isBoilingAtmo(),
                                                  mass,
                                                  atmosphericComposition);

        // TODO Special considerations for c objects, this should be expanded upon when these gets more details
        var albedo = findAlbedo(IS_INNER_ZONE, atmoPressure, hydrosphereDescription, hydrosphere);
        moonBuilder.albedo(BigDecimal.valueOf(albedo).round(TWO));

        baseTemperature = (int)( baseTemperature*albedo);

        if (orbitalObjectClass == 'c') { //These should never had a chance to get an "real" atmosphere in the first
            // place but may have some traces
            if (Dice.d6(6)) atmoPressure = 0;
            else atmoPressure = 0.001;
        }

        if (atmoPressure == 0) atmosphericComposition.clear();
        if (atmosphericComposition.size() == 0) { //There are edge cases where all of atmo has boiled away
            atmoPressure = 0;
            moonBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
            moonBuilder.hydrosphere(0);
        }

        // The composition could be adjusted for the existence of life, so is set below
        double systemAge = ((Star) (orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround()))
                .getAge()
                .doubleValue();

        //Bioshpere

        var hasGaia = false;
        if (atmoPressure > 0) hasGaia = LifeMethods.testLife(baseTemperature,
                                                                           atmoPressure,
                                                                           hydrosphere,
                                                                           atmosphericComposition,
                                                                           systemAge,
                                                                           magneticField);
        if (hasGaia) lifeType = LifeMethods.findLifeType(atmosphericComposition, systemAge);
        else lifeType = Breathing.NONE;

        if (lifeType.equals(Breathing.OXYGEN)){
            int oxygen = adjustForOxygen(atmoPressure, atmosphericComposition);
            atmoPressure += oxygen;
        }

        double greenhouseFactor = MakeAtmosphere.findGreenhouseGases(atmosphericComposition,
                                                                     atmoPressure,
                                                                     baseTemperature,
                                                                     hydrosphereDescription,
                                                                     hydrosphere,
                                                                     hasGaia);

        int surfaceTemp = TempertureMethods.getSurfaceTemp(baseTemperature,
                                                           atmoPressure,
                                                           greenhouseFactor,
                                                           hasGaia,
                                                           lifeType);

        var temperatureFacts = TempertureMethods.setSeasonalTemperature(atmoPressure,
                                                                        hydrosphere,
                                                                        rotationalPeriod,
                                                                        axialTilt,
                                                                        surfaceTemp,
                                                                        lunarOrbitalPeriod);
        temperatureFacts.eccentricityVariation(TempertureMethods.setExcentricityVariation(eccentricity,
                                                                                          orbitDistance.doubleValue(),
                                                                                          centralStar.getLuminosity().doubleValue()));

        if (!atmosphericComposition.isEmpty()) checkAtmo(atmosphericComposition, atmoPressure);

        moonBuilder.atmosphericComposition(atmosphericComposition);
        moonBuilder.lifeType(lifeType);

        //Climate -------------------------------------------------------
        // sets all the temperature stuff from axial tilt etc etc TODO should take the special circumstances of moons too


        temperatureFacts.surfaceTemp(surfaceTemp);
        //TODO Weather and day night temp cycle
        TempertureMethods.setDayNightTemp(temperatureFacts,
                                          baseTemperature,
                                          centralStar.getLuminosity().doubleValue(),
                                          orbitDistance.doubleValue(),
                                          atmoPressure,
                                          rotationalPeriod);

        checkHydrographics(hydrosphereDescription,
                           hydrosphere,
                           atmoPressure,
                           moonBuilder,
                           surfaceTemp,
                           temperatureFacts.build().getRangeBandTempWinter(),
                           temperatureFacts.build().getRangeBandTempSummer());

        checkHydrographics(hydrosphereDescription,
                           hydrosphere,
                           atmoPressure,
                           moonBuilder,
                           surfaceTemp,
                           temperatureFacts.build().getRangeBandTempWinter(),
                           temperatureFacts.build().getRangeBandTempSummer());

        if (!atmosphericComposition.isEmpty()) MakeAtmosphere.checkAtmo(atmosphericComposition, atmoPressure);

        if (lifeType != Breathing.NONE) {
            var biosphere = Biosphere.builder()
                                     .homeworld(name)
                                     .respiration(lifeType)
                                     .baseElement(surfaceTemp<360 ? BaseElementOfLife.CARBON : BaseElementOfLife.SILICA);
            moonBuilder.life(biosphere.build());
        }

        moonBuilder.orbitalFacts(orbitalFacts.build());
        moonBuilder.temperatureFacts(temperatureFacts.build());
        moonBuilder.atmoPressure(BigDecimal.valueOf(atmoPressure).round(THREE));


        return moonBuilder.build();
    }

    private static int getEccentryMod(char orbitalObjectClass) {
        int eccentryMod = 1;
        if (orbitalObjectClass == 'C' || orbitalObjectClass == 'c') eccentryMod += 3;
        return eccentryMod;
    }

    private static int getBaseSize(char orbitalObjectClass) {

        //TODO add greater varity for moon objects, depending on planet
        int baseSize = 10;
        if (orbitalObjectClass == 'M') {
            List<Integer> baseSizeList = Arrays.asList(150, 200, 300, 400, 500, 600, 700, 800, 900, 1000);
            baseSize = TableMaker.makeRoll(Dice.d10(), baseSizeList);

        } else if (orbitalObjectClass == 'm') {
            List<Integer> baseSizeList = Arrays.asList(5, 10, 15, 20, 25, 30, 40, 50, 60, 75);
            baseSize = TableMaker.makeRoll(Dice.d10(), baseSizeList);

        } else if (orbitalObjectClass == 'c') baseSize = 90;
        return baseSize;
    }


}
