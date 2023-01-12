package com.github.hteph.starsys.utils;

import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.StellarObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThymeleafUtils {

    public static List<Biosphere> getLife(List<StellarObject> systemList) {

        var planetStream = systemList.stream()
                                     .filter(Planet.class::isInstance);

        var moonStream = systemList.stream()
                                   .filter(Planet.class::isInstance)
                                   .map(stellarObject -> ((Planet) stellarObject).getMoonList())
                                   .flatMap(Collection::stream);


        return Stream.concat(planetStream, moonStream)
                     .map(planet -> ((Planet) planet).getLife())
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());

    }

    public static boolean hasMoons(List<StellarObject> systemList) {
        return systemList.stream()
                         .filter(Planet.class::isInstance)
                         .anyMatch(p -> ((Planet) p).getMoonList() != null && !((Planet) p).getMoonList().isEmpty());
    }
}