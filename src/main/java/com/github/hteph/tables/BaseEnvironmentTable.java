package com.github.hteph.tables;



import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.wrappers.Homeworld;
import com.github.hteph.utils.enums.BaseElementOfLife;
import com.github.hteph.utils.enums.Breathing;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static com.github.hteph.utils.enums.EnvironmentalEnum.*;

@Slf4j
public class BaseEnvironmentTable {

    private Homeworld place;
    private Biosphere biosphere;

    public BaseEnvironmentTable(Biosphere biosphere) {

        this.place =biosphere.getHomeworld();
        this.biosphere = biosphere;
    }

    public EnvironmentalEnum[] findBaseEnvironment(){


        if(place.getStellarObjectType()== StellarObjectType.TERRESTRIAL
                && biosphere.getRespiration() != Breathing.AMMONIA
        && biosphere.getBaseElement() == BaseElementOfLife.CARBON){
            return findTerrestialPlanetbaseEnvironment(place);
        }

        return new EnvironmentalEnum[]{EnvironmentalEnum.EXOTIC, NONE};

    }

    private EnvironmentalEnum[] findTerrestialPlanetbaseEnvironment(Homeworld place) {

        TreeMap<Integer, EnvironmentalEnum> map = new TreeMap<>();

        for (EnvironmentalEnum anEnum : EnvironmentalEnum.values()) {
            int chance = 10;
            var enumClass = anEnum.getClassification();
            if (enumClass.contains("Z")) continue; // No civilisation base environments sophonts yet
            if (enumClass.contains("H") && place.getHydrosphere() < 5) continue;
            if (enumClass.contains("h")) chance +=  place.getHydrosphere() / 10;
            if (enumClass.contains("d")) chance -= place.getHydrosphere() / 10;
            if (enumClass.contains("c")) chance = (int) (chance / place.getTemperatureFacts().getSurfaceTemp() / 274.0);
            if (enumClass.contains("t")) chance = (int) (chance * place.getTemperatureFacts().getSurfaceTemp() / 274.0);
            if (enumClass.contains("u")) chance = (int) (chance * Math.random());
            //Add tectonics to chance of mountains?

            if(chance<1 || anEnum==NONE) continue;

            if (map.isEmpty()) map.put(chance, anEnum);
            else {
                map.put(map.lastKey() + chance, anEnum);
            }
        }

//Take one out of the mix
        var randomDraw = (int) (Math.random() * map.lastKey());
        var keyToUse = randomDraw < map.firstKey()? map.firstKey() : randomDraw;
        var theDraw = map.floorKey(keyToUse);
        EnvironmentalEnum prim = map.remove(theDraw);

//If prim needs a sceondary terrain, draw one from those who remains.
// TODO Here a realism check should be added...Deep ocean and mountains frx.
        EnvironmentalEnum sec = NONE;
        if(prim == EnvironmentalEnum.ALPINE){
            Set<EnvironmentalEnum> forbiddenList = new HashSet<>(Arrays.asList(
                    REEFS,
                    SHELVES,
                    DEEP_OCEAN,
                    MANAGED_AQUATIC,
                    CAVE,
                    EXOTIC,
                    BIOINDUSTRIAL,
                    GREENHOUSE,
                    TREE_CROP,
                    NONE,
                    FIELD_CROP,
                    MANAGED_GRASSLANDS));
            do {
                sec = map.get(map.floorKey((int) (1 + Math.random() * map.lastKey())));
            }while(forbiddenList.contains(sec));
        }else if(prim == RIVER_AND_STREAM
                || prim == LAKES
                || prim == COASTAL){
            Set<EnvironmentalEnum> forbiddenList = new HashSet<>(Arrays.asList(
                    TEMPERATE_AND_SEMI_DESERTS,
                    DESERT,
                    REEFS,
                    SHELVES,
                    DEEP_OCEAN,
                    MANAGED_AQUATIC,
                    CAVE,
                    NONE,
                    EXOTIC,
                    BIOINDUSTRIAL,
                    GREENHOUSE,
                    TREE_CROP,
                    FIELD_CROP,
                    MANAGED_GRASSLANDS));
            do {
                sec = map.get(map.floorKey((int) (1 + Math.random() * map.lastKey())));
            }while(forbiddenList.contains(sec));
        }
        return new EnvironmentalEnum[]{prim, sec};
    }
}