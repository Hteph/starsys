package com.github.hteph.generators;


import com.github.hteph.generators.utils.LifeMethods;
import com.github.hteph.generators.utils.MakeAtmosphere;
import com.github.hteph.generators.utils.PlanetaryUtils;
import com.github.hteph.generators.utils.TempertureMethods;
import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.repository.objects.wrappers.Homeworld;
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
import java.util.Arrays;
import java.util.List;

import static com.github.hteph.generators.utils.MakeAtmosphere.*;
import static com.github.hteph.generators.utils.PlanetaryUtils.*;
import static com.github.hteph.utils.NumberUtilities.*;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class MoonFactory {
    
    public static Planet generate(
        final String archiveID,
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
        
        double moonsPlanetMass;
        double lunarOrbitalPeriod;
        
        double moonsPlanetsRadii;
        
        Breathing lifeType; //TODO make this an Optional
        double tidalForce = 0;
        
        
        var moonBuilder = Planet.builder()
                                .archiveID(archiveID)
                                .name(name)
                                .stellarObjectType(StellarObjectType.MOON)
                                .description(description)
                                .classificationName(classificationName)
                                .lunarOrbitDistance(BigDecimal.valueOf(lunarOrbitNumberInPlanetRadii).round(THREE));
        
        var luminosity = ((Star) (orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround())).getLuminosity();
        
        var orbitalFacts = OrbitalFacts.builder()
                                       .orbitsAround(orbitingAroundPlanet)
                                       .orbitalPeriod(orbitingAroundPlanet.getOrbitalFacts().getOrbitalPeriod());
        
        moonsPlanetMass = orbitingAroundPlanet.getMass().doubleValue();
        moonsPlanetsRadii = orbitingAroundPlanet.getRadius();
        
        //TODO sort that equation out
        var moonTidal = (moonsPlanetMass * 26640000 / 333000.0 /
            Math.pow(moonsPlanetsRadii * lunarOrbitNumberInPlanetRadii * 400 / 149600000, 3));
        moonBuilder.lunarTidal(BigDecimal.valueOf(moonTidal).round(THREE));
        
        var centralStar = (Star) orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround();
        
        final double SNOWLINE = 5 * sqrt(centralStar.getLuminosity().doubleValue());
        final boolean IS_INNER_ZONE = orbitDistance.doubleValue() < SNOWLINE;
        
        // size may not be all, but here it is set
        //TODO add greater varity for moon objects, depending on planet
        final int moonRadius =
            Math.min(orbitingAroundPlanet.getRadius() / 3, Dice._2d6() * getBaseSize(orbitalObjectClass));
        moonBuilder.radius(moonRadius);
        
        //density
        final double density = getDensity(orbitDistance, centralStar, SNOWLINE);
        final double mass = cubed(moonRadius / 6380.0) * density;
        final double gravity = mass / squared((moonRadius / 6380.0));
        
        //I assume this is in Earth days?
        lunarOrbitalPeriod = sqrt(
            cubed((lunarOrbitNumberInPlanetRadii * moonsPlanetsRadii) / 400000) * 793.64 / (moonsPlanetMass + mass));
        
        moonBuilder.mass(BigDecimal.valueOf(mass).round(THREE))
                   .gravity(BigDecimal.valueOf(gravity).round(TWO))
                   .density(BigDecimal.valueOf(density).round(TWO))
                   .lunarOrbitalPeriod(BigDecimal.valueOf(lunarOrbitalPeriod).round(THREE));
        
        //Eccentricity and Inclination TODO Are these actually Ok for moons?
        //Simplify by reducing the axial tilt of moons as it matters less for their climate.
        final double axialTilt = (int) (Dice._3d6() * Math.random());
        final int eccentryMod = getEccentryMod(orbitalObjectClass);
        double eccentricity = eccentryMod * (Dice._2d6() - 2) / (100.0 * Dice.d6());
        
        moonBuilder.axialTilt(BigDecimal.valueOf(axialTilt).round(THREE));
        orbitalFacts.orbitalInclination(BigDecimal.valueOf(eccentryMod * (Dice._2d6()) / (1.0 + mass / 10.0))
                                                  .round(THREE));
        
        if (moonTidal > 1) {planetLocked = true;}
        moonBuilder.planetLocked(planetLocked);
        
        double rotationalPeriod;
        //Rotation - day/night cycle
        if (planetLocked) {
            rotationalPeriod = lunarOrbitalPeriod * 24;
        } else {
            rotationalPeriod = (Dice.d6() + Dice.d6() + 8) *
                (1 + 0.1 * (tidalForce * centralStar.getAge().doubleValue() - sqrt(mass)));
            
            if (Dice.d6(2)) {rotationalPeriod = Math.pow(rotationalPeriod, Dice.d6());}
            
            if (rotationalPeriod > lunarOrbitalPeriod / 2.0) {
                
                double[] resonanceArray = {0.5, 2 / 3.0, 1, 1.5, 2, 2.5, 3, 3.5};
                double[] eccentricityEffect = {0.1, 0.15, 0.21, 0.39, 0.57, 0.72, 0.87, 0.87};
                
                int resultResonance = Arrays.binarySearch(resonanceArray, rotationalPeriod / lunarOrbitalPeriod);
                
                if (resultResonance < 0) {
                    eccentricity = eccentricityEffect[-resultResonance - 2];
                    rotationalPeriod = resonanceArray[-resultResonance - 2];
                } else {
                    resultResonance =
                        Math.min(resultResonance, 6); //TODO there is something fishy here, Edge case of greater than
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
            mass
        );
    
        //adding magnetic field from planet (where it exists i.e Jovians) for the forthcoming calculations
        if (orbitingAroundPlanet.getMagneticField() != null) {
            magneticField += orbitingAroundPlanet.getMagneticField().doubleValue();
        }
    
        moonBuilder.tectonicCore(tectonicCore)
                   .tectonicActivityGroup(tectonicActivityGroup)
                   .magneticField(BigDecimal.valueOf(magneticField).round(TWO));
    
        //Temperature
        //base temp is an value of little use beyond this generator and is not propagated to the planet object
        baseTemperature = TempertureMethods.findBaseTemp(orbitDistance.doubleValue(), luminosity.doubleValue());
        
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
            moonBuilder
        );
        var tempMoon = moonBuilder.build();
        
        double atmoPressure = FindAtmoPressure.calculate(tectonicActivityGroup,
            hydrosphere,
            tempMoon.isBoilingAtmo(),
            mass,
            atmosphericComposition
        );
        
        // TODO Special considerations for c objects, this should be expanded upon when these gets more details
        var albedo = findAlbedo(IS_INNER_ZONE, atmoPressure, hydrosphereDescription, hydrosphere);
        moonBuilder.albedo(BigDecimal.valueOf(albedo).round(TWO));
        
        baseTemperature = (int) (baseTemperature * albedo);
        
        if (orbitalObjectClass == 'c') { //These should never had a chance to get an "real" atmosphere in the first
            // place but may have some traces
            if (Dice.d6(6)) {atmoPressure = 0;} else {atmoPressure = 0.001;}
        }
        
        if (atmoPressure == 0) {atmosphericComposition.clear();}
        if (atmosphericComposition.isEmpty()) { //There are edge cases where all of atmo has boiled away
            atmoPressure = 0;
            moonBuilder.hydrosphereDescription(HydrosphereDescription.REMNANTS);
            moonBuilder.hydrosphere(0);
        }
        
        // The composition could be adjusted for the existence of life, so is set below
        double systemAge = ((Star) (orbitingAroundPlanet.getOrbitalFacts().getOrbitsAround())).getAge().doubleValue();
        
        double greenhouseFactor = MakeAtmosphere.findGreenhouseGases(atmosphericComposition,
            atmoPressure,
            baseTemperature,
            hydrosphereDescription,
            hydrosphere
        );
        
        int surfaceTemp = TempertureMethods.getSurfaceTemp(baseTemperature, atmoPressure, greenhouseFactor);
        //Bioshpere
        
        var hasGaia = false;
        if (atmoPressure > 0) {
            hasGaia = LifeMethods.testLife(surfaceTemp,
                atmoPressure,
                hydrosphere,
                atmosphericComposition,
                systemAge,
                magneticField,
                tectonicActivityGroup
            );
        }
        var biosphere = Biosphere.builder();
        if (hasGaia) {
            lifeType = LifeMethods.findLifeType(atmosphericComposition, centralStar.getAge().doubleValue());
            if (lifeType.equals(Breathing.OXYGEN)) {
                int oxygen = MakeAtmosphere.adjustForOxygen(atmoPressure, atmosphericComposition);
                atmoPressure *= 1 + oxygen / 100d; //completly invented buff for atmopressure of oxygen breathers
            }
            
            biosphere.respiration(lifeType)
                     .baseElement(surfaceTemp < 360 + Dice.d20() ? BaseElementOfLife.CARBON : BaseElementOfLife.SILICA);
        }
        
        
        var temperatureFacts = TempertureMethods.setSeasonalTemperature(atmoPressure,
            hydrosphere,
            rotationalPeriod,
            axialTilt,
            surfaceTemp,
            lunarOrbitalPeriod
        );
        temperatureFacts.eccentricityVariation(TempertureMethods.setExcentricityVariation(eccentricity,
            orbitDistance.doubleValue(),
            centralStar.getLuminosity().doubleValue()
        ));
        
        if (!atmosphericComposition.isEmpty()) {checkAtmo(atmosphericComposition, atmoPressure);}
        
        moonBuilder.atmosphericComposition(atmosphericComposition);
        
        
        //Climate -------------------------------------------------------
        // sets all the temperature stuff from axial tilt etc etc TODO should take the special circumstances of moons
        //  too
        
        
        temperatureFacts.surfaceTemp(surfaceTemp);
        //TODO Weather and day night temp cycle
        TempertureMethods.setDayNightTemp(temperatureFacts,
            baseTemperature,
            centralStar.getLuminosity().doubleValue(),
            orbitDistance.doubleValue(),
            atmoPressure,
            rotationalPeriod
        );
        
        checkHydrographics(hydrosphereDescription,
            hydrosphere,
            atmoPressure,
            moonBuilder,
            surfaceTemp,
            temperatureFacts.build().getRangeBandTempWinter(),
            temperatureFacts.build().getRangeBandTempSummer()
        );
        
        checkHydrographics(hydrosphereDescription,
            hydrosphere,
            atmoPressure,
            moonBuilder,
            surfaceTemp,
            temperatureFacts.build().getRangeBandTempWinter(),
            temperatureFacts.build().getRangeBandTempSummer()
        );
        
        if (!atmosphericComposition.isEmpty()) {MakeAtmosphere.checkAtmo(atmosphericComposition, atmoPressure);}
        moonBuilder.atmoPressure(BigDecimal.valueOf(atmoPressure).round(THREE));
        
        
        moonBuilder.orbitalFacts(orbitalFacts.build());
        moonBuilder.temperatureFacts(temperatureFacts.build());
        
        
        if (hasGaia) {
            var homeworld = Homeworld.builder()
                                     .hydrosphereDescription(hydrosphereDescription)
                                     .name(name)
                                     .stellarObjectType(StellarObjectType.TERRESTRIAL)
                                     .temperatureFacts(temperatureFacts.build())
                                     .gravity(gravity)
                                     .magneticField(magneticField);
            moonBuilder.life(biosphere.homeworld(homeworld.build()).build());
        }
        
        return moonBuilder.build();
    }
    
    private static int getEccentryMod(char orbitalObjectClass) {
        int eccentryMod = 1;
        if (orbitalObjectClass == 'C' || orbitalObjectClass == 'c') {eccentryMod += 3;}
        return eccentryMod;
    }
    
    private static int getBaseSize(char orbitalObjectClass) {
        
        //TODO add greater varity for moon objects, depending on planet
        int baseSize = 10;
        if (orbitalObjectClass == 'M') {
            List<Integer> baseSizeList = Arrays.asList(420, 450, 500, 550, 600, 650, 700, 750, 800, 900, 1000);
            baseSize = TableMaker.makeRoll(Dice.d10(), baseSizeList);
            
        } else if (orbitalObjectClass == 'm') {
            List<Integer> baseSizeList = Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 50, 60, 70);
            baseSize = TableMaker.makeRoll(Dice.d10(), baseSizeList);
            
        } else if (orbitalObjectClass == 'c') {baseSize = 90;}
        return baseSize;
    }
    
    
}
