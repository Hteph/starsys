package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.TemperatureFacts;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.Breathing;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import static com.github.hteph.utils.NumberUtilities.sqrt;

public class TempertureMethods {

    public static int findBaseTemp(double orbitalDistance, double luminosity) {

        return (int) (255 / sqrt((orbitalDistance / sqrt(luminosity))));
    }

    public static TemperatureFacts.TemperatureFactsBuilder setSeasonalTemperature(double atmoPressure,
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

        int baseModeration = 0;
        baseModeration += (hydrosphere - 60) / 10;
        baseModeration += (atmoPressure < 0.1) ? -3 : 1;
        baseModeration += (int) atmoPressure;
        baseModeration += (rotationalPeriod < 10) ? -3 : 1;
        baseModeration += (int) (Math.sqrt(rotationalPeriod / 24)); //Shouldn't this be negative?
        baseModeration += (int) (10 / axialTilt);

        String atmoModeration;
        if (atmoPressure == 0) atmoModeration = "No";
        else if (atmoPressure > 10) atmoModeration = "Extreme";
        else if (baseModeration < -2) atmoModeration = "Low";
        else if (baseModeration > 2) atmoModeration = "High";
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


    public static int getSurfaceTemp(int baseTemperature,
                                     double atmoPressure,
                                     double greenhouseFactor,
                                     boolean hasGaia,
                                     Breathing lifeType) {

        //TODO Here adding some Gaia moderation factor (needs tweaking probably) moving a bit more towards
        // water/carbon ideal
        if (lifeType.equals(Breathing.OXYGEN) && baseTemperature > 350) greenhouseFactor *= 0.8;
        if (lifeType.equals(Breathing.OXYGEN) && baseTemperature < 250) greenhouseFactor *= 1.2;

        // My take on the effect of greenhouse and albedo on temperature max planerary temp is 1000 and the half
        // point is 400
        double surfaceTemp;
        if (hasGaia) {
            double a = 3.94935;
            double b = 304.305;
            double t = -2.88013;

            int modTemp = (int) (baseTemperature * NumberUtilities.sqrt(1 + greenhouseFactor));
            surfaceTemp = 100 * (int) (a / (1 + b * Math.exp(t * (modTemp / 100d))));
            //surfaceTemp = 400d * (baseTemperature  * greenhouseFactor) / (350d + baseTemperature * greenhouseFactor);
        } else if (atmoPressure > 0) {
            surfaceTemp = 800d * (baseTemperature * greenhouseFactor)
                    / (400d + baseTemperature * greenhouseFactor);
        } else {
            surfaceTemp = 1200d * (baseTemperature * greenhouseFactor)
                    / (800d + baseTemperature * greenhouseFactor);
        }

        return (int) surfaceTemp;
    }

    public static void setDayNightTemp(TemperatureFacts.TemperatureFactsBuilder tempTempFacts, int baseTemperature, double luminosity, double orbitDistance, double atmoPressure, double rotationPeriod) {

        //Assymerical sigmoidal:  5-parameter logistic (5PL)



        var increasePerHourFactor = -1.554015 + (0.9854966 - -1.554015) / Math.pow(1 + Math.pow(atmoPressure / 19056230d, 0.5134927), 1094.463);
        increasePerHourFactor = Math.max(0.01,increasePerHourFactor);
        var maxDayIncreaseMultiple = 7.711577 + (0.2199364 - 7.711577) / Math.pow(1 + Math.pow(atmoPressure / 2017503d, 1.004679), 757641.3);
        var incomingRadiation = luminosity / sqrt(orbitDistance);
        System.out.println("increasePerHourFactor =" + increasePerHourFactor + ", maxDayIncreaseMultiple=" + maxDayIncreaseMultiple + ", incomingRadiation=" + incomingRadiation);
        var daytimeMax = Math.min(incomingRadiation * increasePerHourFactor * rotationPeriod / 2d,
                                  Math.min(1000+ Dice._2d6()*25, baseTemperature * incomingRadiation * maxDayIncreaseMultiple));
        System.out.println("DayTemperatue by day=" + incomingRadiation * increasePerHourFactor * rotationPeriod / 2d + " but max=" + baseTemperature * incomingRadiation * maxDayIncreaseMultiple);



        var decresePerHour = -0.5906138 + (19.28838 - -0.5906138) / Math.pow(1 + Math.pow(atmoPressure / 291099200d, 0.5804294), 172207.2);
        decresePerHour = Math.min(decresePerHour,0.1);
        var maxNigthDecreaseMultiple = 0.03501408 + (0.7690167 - 0.03501408) / Math.pow(1 + Math.pow(atmoPressure / 6815738d, 0.7782145), 322006.2);

        var nighttimeMin = -Math.min(decresePerHour * rotationPeriod / 2d,
                                     maxNigthDecreaseMultiple * baseTemperature);

        System.out.println("Temperatue by night=" + decresePerHour * rotationPeriod / 2d + " but max=" + maxNigthDecreaseMultiple * baseTemperature);

        tempTempFacts.dayNightVariation(TemperatureFacts.Variation.builder()
                                                                  .max((int) daytimeMax)
                                                                  .min((int) nighttimeMin)
                                                                  .build());
    }

    public static TemperatureFacts.Variation setExcentricityVariation(double eccentricity,
                                                                      double orbitDistance,
                                                                      double luminosity) {

        var base = findBaseTemp(orbitDistance, luminosity);
        var variation = TemperatureFacts.Variation.builder();

        variation.max(findBaseTemp(orbitDistance * (1 - eccentricity), luminosity) - base);
        variation.min(findBaseTemp(orbitDistance * (1 + eccentricity), luminosity) - base);

        return variation.build();
    }
}