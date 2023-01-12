package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.tables.TectonicActivityTable;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.HydrosphereDescription;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.github.hteph.utils.NumberUtilities.sqrt;
import static com.github.hteph.utils.NumberUtilities.squared;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanetaryUtils {

    public static String findTectonicGroup(boolean innerZone, double density) {

        String tempTectonics;
        if (innerZone) {
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

    public static double findAlbedo(boolean innerZone,
                                    double atmoPressure,
                                    HydrosphereDescription hydrosphereDescription,
                                    int hydrosphere) {

        int mod = 0;
        int[] randAlbedoArray;
        Double[] albedoBase = new Double[]{0.75, 0.85, 0.95, 1.05, 1.15};

        if (innerZone) {
            randAlbedoArray = new int[]{0, 2, 4, 7, 10};

            if (atmoPressure < 0.05) {
                mod = 2;
            }
            if (atmoPressure > 50) {
                mod = -4;
            } else if (atmoPressure > 5) {
                mod = -2;
            }
            if (hydrosphere > 50
                && hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)
                && mod > -2) {
                mod = -2;
            }
            if (hydrosphere > 90
                && hydrosphereDescription.equals(HydrosphereDescription.ICE_SHEET)
                && mod > -4) {
                mod = -4;
            }

        } else {
            randAlbedoArray = new int[]{0, 4, 6, 8, 10};
            if (atmoPressure > 1) {
                mod = 1;
            }
        }
        return TableMaker.makeRoll(Dice._2d6() + mod, randAlbedoArray, albedoBase) + (Dice.d10() - 1) * 0.01;
    }

    public static double getDensity(BigDecimal orbitDistance, Star orbitingAround, double snowLine) {
        double density;
        if (orbitDistance.doubleValue() < snowLine) {
            density = 0.3 + (Dice._2d6() - 2) * 0.127 / Math.pow(
                    0.4 + (orbitDistance.doubleValue() / sqrt(orbitingAround.getLuminosity().doubleValue())), 0.67);
        } else {
            density = 0.3 + (Dice._2d6() - 2) * 0.05;
        }
        return density;
    }

    public static String getTectonicActivityGroup(Star orbitingAround, double tidalForce, double mass) {
        double tectonicActivity;
        String tectonicActivityGroup;
        tectonicActivity = (5 + Dice._2d6() - 2) * Math.pow(mass, 0.5) / orbitingAround.getAge().doubleValue();
        tectonicActivity *= (1 + 0.5 * tidalForce);
        tectonicActivityGroup = TectonicActivityTable.findTectonicActivityGroup(tectonicActivity);
        return tectonicActivityGroup;
    }

    public static double getMagneticField(double rotationalPeriod,
                                          String tectonicCore,
                                          String tectonicActivityGroup,
                                          Star orbitingAround,
                                          double density,
                                          double mass) {

        double magneticField;
        if (tectonicCore.contains("metal")) {
            magneticField = 10 / (sqrt((rotationalPeriod / 24.0)))
                            * squared(density)
                            * sqrt(mass)
                            / orbitingAround.getAge().doubleValue();
            if (tectonicCore.contains("small")) {
                magneticField *= 0.5;
            }
            if (tectonicCore.contains("medium")) {
                magneticField *= 0.75;
            }
            if (tectonicActivityGroup.equals("Dead")) {
                magneticField = Dice.d6() / 15.0;
            }
        } else {
            magneticField = Dice.d6() / 20.0;
        }
        return magneticField;
    }
}
