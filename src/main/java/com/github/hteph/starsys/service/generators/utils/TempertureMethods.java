package com.github.hteph.starsys.service.generators.utils;

import com.github.hteph.starsys.service.objects.TemperatureFacts;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import static com.github.hteph.utils.NumberUtilities.TWO;
import static com.github.hteph.utils.NumberUtilities.sqrt;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    
        int baseModeration = getBaseModeration(atmoPressure, hydrosphere, rotationalPeriod, axialTilt);
    
        String atmoModeration;
        if (atmoPressure == 0) atmoModeration = "No";
        else if (atmoPressure > 10) atmoModeration = "Extreme";
        else if (baseModeration < -2) atmoModeration = "Low";
        else if (baseModeration > 2) atmoModeration = "High";
        else atmoModeration = "Average";

        int atmoIndex = switch (atmoModeration) {
            case "High" -> 2;
            case "Average" -> 1;
            case "Extreme" -> 3;
            default -> 0;
        };
    
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
    
    private static int getBaseModeration(
        double atmoPressure,
        int hydrosphere,
        double rotationalPeriod,
        double axialTilt) {
        
        int baseModeration = 0;
        baseModeration += (hydrosphere - 60) / 10;
        baseModeration += (atmoPressure < 0.1) ? -3 : 1;
        baseModeration += (int) atmoPressure;
        baseModeration += (rotationalPeriod < 10) ? -3 : 1;
        baseModeration += (int) (Math.sqrt(rotationalPeriod / 24)); //Shouldn't this be negative?
        baseModeration += (int) (10 / axialTilt);
        return baseModeration;
    }
    
    
    public static int getSurfaceTemp(int baseTemperature,
                                     double atmoPressure,
                                     double greenhouseFactor) {


        double surfaceTemp;
        if (atmoPressure > 0 && baseTemperature > 220 && baseTemperature < 400) {

            surfaceTemp = (int) (baseTemperature * NumberUtilities.sqrt(0.2 + greenhouseFactor));

        } else if (atmoPressure > 0) {
            surfaceTemp = 800d * (baseTemperature * greenhouseFactor)
                    / (400d + baseTemperature * greenhouseFactor);
        } else {
            surfaceTemp = 1200d * (baseTemperature * greenhouseFactor)
                    / (800d + baseTemperature * greenhouseFactor);
        }
        
        if(Math.abs( surfaceTemp-baseTemperature) >50 ) log.debug("Difference in base and actual temperature, base = {}, actual = {}, greenhouse = {}",baseTemperature, surfaceTemp, greenhouseFactor);
        
        return (int) surfaceTemp;
    }

    public static void setDayNightTemp(TemperatureFacts.TemperatureFactsBuilder tempTempFacts, int baseTemperature, double luminosity, double orbitDistance, double atmoPressure, double rotationPeriod) {

        //Assymerical sigmoidal:  5-parameter logistic (5PL)

        var increasePerHourFactor = -1.554015 + (0.9854966 - -1.554015) / Math.pow(1 + Math.pow(atmoPressure / 19056230d, 0.5134927), 1094.463);
        increasePerHourFactor = Math.max(0.01, increasePerHourFactor);
        var maxDayIncreaseMultiple = 7.711577 + (0.2199364 - 7.711577) / Math.pow(1 + Math.pow(atmoPressure / 2017503d, 1.004679), 757641.3);
        var incomingRadiation = luminosity / sqrt(orbitDistance);
        var daytimeMax = Math.min(incomingRadiation * increasePerHourFactor * rotationPeriod / 2d,
                                  Math.min(1000 + Dice._2d6() * 25.0, baseTemperature * incomingRadiation * maxDayIncreaseMultiple));


        var decresePerHour = -0.5906138 + (19.28838 - -0.5906138) / Math.pow(1 + Math.pow(atmoPressure / 291099200d, 0.5804294), 172207.2);
        decresePerHour = Math.max(decresePerHour, 0.1);
        var maxNigthDecreaseMultiple = 0.03501408 + (0.7690167 - 0.03501408) / Math.pow(1 + Math.pow(atmoPressure / 6815738d, 0.7782145), 322006.2);

        var nighttimeMin = -Math.min(decresePerHour * rotationPeriod / 2d,
                                     maxNigthDecreaseMultiple * baseTemperature);

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

    public static List<BigDecimal> getDayTempCurve(double dayLength, double emissions, double incoming) {

        int localHour = (int) Math.ceil(dayLength);

        double[] hourlyTempArray = new double[localHour];

        double daytempAmplitude = (1d * incoming);

        hourlyTempArray[0] = 0;
        ArrayList<BigDecimal> tempProfile = new ArrayList<>();

        for (int i = 1; i < localHour; i++) {

            double daytimePlus = Math.sin(-Math.PI / 2 + 2 * Math.PI * i / localHour) * daytempAmplitude;

            var change = (daytimePlus > 0 ? daytimePlus : 0) - emissions;

            double surplusEmmisons = 0;
            if (i > localHour / 2) {

                var tempEmisson = (hourlyTempArray[i])/(2*(localHour-i));
                surplusEmmisons = tempEmisson>(emissions*(localHour-i))?tempEmisson:0;
            }

            hourlyTempArray[i] = hourlyTempArray[i - 1] + change - surplusEmmisons;

            tempProfile.add(BigDecimal.valueOf(hourlyTempArray[i]).round(TWO));
        }

        return tempProfile;
    }
}