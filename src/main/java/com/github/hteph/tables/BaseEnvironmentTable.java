package com.github.hteph.tables;



import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.wrappers.Homeworld;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.StellarObjectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static com.github.hteph.utils.enums.EnvironmentalEnum.*;


public class BaseEnvironmentTable {

    private Homeworld place;

    public BaseEnvironmentTable(Homeworld planet) {

        this.place =planet;
    }

    public EnvironmentalEnum[] findBaseEnvironment(){

        if(place.getStellarObjectType()== StellarObjectType.TERRESTRIAL) return findTerrestialPlanetbaseEnvironment(place);

        return new EnvironmentalEnum[]{EnvironmentalEnum.EXOTIC, NONE};

    }

    private EnvironmentalEnum[] findTerrestialPlanetbaseEnvironment(Homeworld place) {

        TreeMap<Integer, EnvironmentalEnum> map = new TreeMap<>();

        for (EnvironmentalEnum anEnum : EnvironmentalEnum.values()) {
            int chance = 10;
            if (anEnum.getClassification().contains("Z")) continue; // No civilisation base environments sophonts yet
            if (anEnum.getClassification().contains("H") && place.getHydrosphere() < 5) continue;
            if (anEnum.getClassification().contains("h")) chance += place.getHydrosphere() / 10;
            if (anEnum.getClassification().contains("d")) chance -= place.getHydrosphere() / 10;
            if (anEnum.getClassification().contains("c")) chance = (int) (chance / place.getTemperatureFacts().getSurfaceTemp() / 274.0);
            if (anEnum.getClassification().contains("t")) chance = (int) (chance * place.getTemperatureFacts().getSurfaceTemp() / 274.0);
            if (anEnum.getClassification().contains("u")) chance = (int) (chance * Math.random());
            //Add tectonics to chance of mountains?

            if(chance<1 ||anEnum==NONE) continue;

            if (map.isEmpty()) map.put(chance, anEnum);
            else {
                map.put(map.lastKey() + chance, anEnum);
            }
        }


//Take one out of the mix
        EnvironmentalEnum prim = map.remove(map.floorKey((int) (1 + Math.random() * map.lastKey())));

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