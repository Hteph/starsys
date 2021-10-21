package com.github.hteph.starsys.utils;

import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.StellarObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThymeleafUtils {
    public ThymeleafUtils() {
    }

    public static List<Biosphere> getLife(ArrayList<StellarObject> systemList) {

        var planetStream = systemList.stream()
                                     .filter(stellarObject -> stellarObject instanceof Planet);

        var moonStream = systemList.stream()
                                   .filter(stellarObject -> stellarObject instanceof Planet)
                                   .map(stellarObject -> ((Planet) stellarObject).getMoonList())
                                   .flatMap(Collection::stream);


        return Stream.concat(planetStream, moonStream)
                     .map(planet -> ((Planet) planet).getLife())
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());

    }

    public static boolean hasMoons(ArrayList<StellarObject> systemList) {
        return systemList.stream()
                         .filter(o -> o instanceof Planet)
                         .anyMatch(p -> ((Planet) p).getMoonList() != null && !((Planet) p).getMoonList().isEmpty());
    }
}