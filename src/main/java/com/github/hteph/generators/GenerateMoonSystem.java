package com.github.hteph.generators;

import com.codepoetics.protonpack.StreamUtils;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.utils.Dice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class GenerateMoonSystem {
    static List<Planet> terrestialMoons(Planet planet, BigDecimal distanceToStar, boolean innerZone) {
        int numberOfMoons;
        if (innerZone) {
            numberOfMoons = (Dice._2d6() - 5) / 2;
        } else {
            numberOfMoons = (int) ((Dice._2d6() - 2) / 1.5);
        }

        List<Character> moonSizes;
        if (numberOfMoons < 1) return Collections.emptyList();
        else moonSizes = setMoonSizes(numberOfMoons);

        double startDistanceInPlanetsRadii = 3 + 150 / (1.0 * moonSizes.size()) * (Dice._2d6() - 2) / 10.0;
        return StreamUtils.zipWithIndex(DoubleStream
                                                .iterate(startDistanceInPlanetsRadii, d -> d + 1 + 150 / (1.0 * moonSizes
                                                        .size()) * (Dice._2d6() - 2) / 10.0)
                                                .limit(moonSizes.size())
                                                .boxed())
                          .map(s -> createMoon(planet,
                                               distanceToStar,
                                               s.getValue(),
                                               s.getIndex(),
                                               moonSizes.get((int) s.getIndex())))
                          .collect(Collectors.toList());
    }

    private static List<Character> setMoonSizes(int numberOfMoons) {
        List<Character> sizeList = new ArrayList<>();

        for (int i = 0; i < numberOfMoons; i++) sizeList.add(Dice.d6(3) ? 'M' : 'm');

        if (sizeList.size() > 2 && Collections.frequency(sizeList, 'M') > 1) {
            //it is difficult to imagine a stable moon configuration with a lot of large moons, and even the
            //reduced one below is probably stretching things. But a lot of moons are a trope we all love
            sizeList = List.of('M', 'M', 'm');
        }
        return sizeList;
    }

    static List<Planet> jovianMoons(Planet planet, BigDecimal distanceToStar, boolean innerZone) {

        //TODO add roche limit and planetary rings

        int divisor = innerZone ? 2 : 1;

        int innerMoonGroup = (Dice._2d6() - 2) / divisor;
        int majorMoonGroup = (Dice.aLotOfd3(2) - 1) / divisor;
        int outerMoonGroup = (Dice.d10() * Dice.d6()) / divisor;

        List<Character> sizeList = new ArrayList<>();

        for (int i = 0; i < innerMoonGroup; i++) sizeList.add('m');
        for (int i = 0; i < majorMoonGroup; i++) sizeList.add('M');
        for (int i = 0; i < outerMoonGroup; i++) sizeList.add('m'); //TODO here there should be 'c' included


        double startDistance = 3 + 150 / (1.0 * sizeList.size()) * (Dice._2d6() - 2) / 10.0;
        return StreamUtils.zipWithIndex(DoubleStream
                                                .iterate(startDistance, d -> d + 1 + 150 / (1.0 * sizeList.size()) * (Dice
                                                        ._2d6() - 2) / 10.0)
                                                .limit(sizeList.size())
                                                .boxed())
                          .map(s -> createMoon(planet,
                                               distanceToStar,
                                               s.getValue(),
                                               s.getIndex(),
                                               sizeList.get((int) s.getIndex())))
                          .collect(Collectors.toList());
    }

    private static Planet createMoon(Planet planet,
                                     BigDecimal distanceToStar,
                                     double orbitNumberInPlanetRadii,
                                     long index,
                                     char size) {

        final int ASCII_STARTING_NUMBER = 97;
        final int ASCII_ENDING_NUMBER = 122;
        char asciiNumber;
        char asciiNumber2;
        String identifier;

        if (ASCII_STARTING_NUMBER + index > ASCII_ENDING_NUMBER) {
            asciiNumber = (char) (ASCII_STARTING_NUMBER + (int) (index / ASCII_STARTING_NUMBER));
            asciiNumber2 = (char) (ASCII_STARTING_NUMBER + (int) (index-28) % ASCII_STARTING_NUMBER);
            identifier = "" + asciiNumber + asciiNumber2;


            if(index > 54){
                identifier = "0" + index;
            }

        } else {
            identifier = "" + (char) (ASCII_STARTING_NUMBER + index);
        }

        String desc = size == 'm' ? "Minor Moon" : "Major Moon";

        // TODO here a roche limit check should be made!!
        return MoonFactory.generate(planet.getArchiveID() + "." + index,
                                    planet.getName() + "-" + identifier,
                                    desc,
                                    "lunar object",
                                    distanceToStar,
                                    size,
                                    planet,
                                    orbitNumberInPlanetRadii);
    }
}