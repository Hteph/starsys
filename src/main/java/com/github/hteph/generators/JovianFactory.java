package com.github.hteph.generators;

import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.repository.objects.TemperatureFacts;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.hteph.utils.NumberUtilities.cubed;
import static com.github.hteph.utils.NumberUtilities.sqrt;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JovianFactory {

    static Planet get(String archiveID,
                      String name,
                      String description,
                      String classificationName,
                      BigDecimal orbitDistance,
                      char orbitalObjectClass,
                      Star star) {

        //String lifeType; // TODO allow for Jovian life in the future

        final var TWO = new MathContext(2);
        final var THREE = new MathContext(3);
        final var FOUR = new MathContext(4);

        var gasGiantBuilder = Planet.builder()
                                    .archiveID(archiveID)
                                    .name(name)
                                    .description(description)
                                    .stellarObjectType(StellarObjectType.JOVIAN)
                                    .classificationName(classificationName);

        var temperatureFactBuilder = TemperatureFacts.builder();

        var orbitalFacts = OrbitalFacts.builder();
        orbitalFacts.orbitsAround(star);
        orbitalFacts.orbitalDistance(orbitDistance.round(THREE));

        double snowLine = 5 * Math.pow(star.getLuminosity().doubleValue(), 0.5);
        boolean innerZone = orbitDistance.doubleValue() < snowLine;
        int jovianRadius;
        double mass;

        if (orbitalObjectClass == 'J') { //TODO something is off here, check calc vs a source
            mass = 250 * Dice._3d6() + Dice.d10() * 100;
            jovianRadius = (int) (60000 + (Dice.d10() - star.getAge().doubleValue() / 2.0) * 2000);
            gasGiantBuilder.radius(jovianRadius);
            gasGiantBuilder.mass(BigDecimal.valueOf(mass));
        } else {
            jovianRadius = Dice._2d6() * 7000 + Dice.d10()*100;
            mass = cubed(jovianRadius / 6380d)
                    * (innerZone ? 0.1 + (Dice.d10() * 0.025) : 0.08 + (Dice.d10() * 0.025));
            gasGiantBuilder.mass(BigDecimal.valueOf(mass).round(FOUR));
            gasGiantBuilder.radius(jovianRadius);
        }
        orbitalFacts.orbitalPeriod(BigDecimal.valueOf(sqrt(cubed(orbitDistance.doubleValue())
                                                                   / star.getMass().doubleValue()))
                                             .round(FOUR)); //in earth years
//Eccentricity and Inclination

        int eccentryMod = 1; //TODO This should probably be changed for the smaller Jovians

        orbitalFacts.orbitalEccentricity(BigDecimal.valueOf(eccentryMod * (Dice._2d6() - 2) / (100.0 * Dice.d6())));
        gasGiantBuilder.axialTilt(BigDecimal.valueOf((Dice._2d6() - 2) / (1.0 * Dice.d6()))
                                            .round(THREE)); //TODO this should be expanded
        orbitalFacts.orbitalInclination(BigDecimal.valueOf(eccentryMod * (Dice._2d6()) / (1 + mass / 10.0))
                                                  .round(THREE));
//Rotational Period

        double rotationalPeriod = (Dice._2d6() + 8) +Dice.d10()/2d;

        gasGiantBuilder.rotationalPeriod(BigDecimal.valueOf(rotationalPeriod).round(THREE));

//Magnetic field
        //TODO these must be adjusted!
        int[] magneticMassArray = {0, 50, 200, 500};
        Double[] magneticMassArrayMin = {0.1, 0.25, 0.5, 1.5, 1.5};
        Double[] magneticMassArrayMax = {1d, 1.5, 3d, 25d, 25d};

        gasGiantBuilder.magneticField(BigDecimal.valueOf(10*(TableMaker.makeRoll((int) mass, magneticMassArray, magneticMassArrayMax)
                - TableMaker.makeRoll((int) mass, magneticMassArray, magneticMassArrayMin))
                                                                 / 10.0
                                                                 * Dice.d10()).round(TWO));
//Temperature
        temperatureFactBuilder.surfaceTemp(((int) (255 / sqrt((orbitDistance.doubleValue()
                / sqrt(star.getLuminosity().doubleValue()))))));

        gasGiantBuilder.orbitalFacts(orbitalFacts.build());

        gasGiantBuilder.moonList(GenerateMoonSystem.jovianMoons(gasGiantBuilder.build(),orbitDistance, innerZone));

        gasGiantBuilder.temperatureFacts(temperatureFactBuilder.build());
        return gasGiantBuilder.build();

    }
}
