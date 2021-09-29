package com.github.hteph.tables;

import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;

@Slf4j
public class StarClassificationTable {

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(60000, "X"); //This should be implemented as some special case, a black hole etc
        map.put(30000, "O"); //Problematic so these are cut out from the equation for now
        map.put(10000, "B");
        map.put(7500, "A");
        map.put(6000, "F");
        map.put(5200, "G");
        map.put(3700, "K");
        map.put(2400, "M");
        map.put(1300, "L");
        map.put(250, "T");
        map.put(0, "Y");
    }

    public static String findStarClass(int temperature) {

        Integer topTemp = 0;
        Integer baseTemp = 0;
        int deciNumber;

        try {
            baseTemp = map.floorKey(temperature);
            topTemp = map.ceilingKey(temperature);

            deciNumber = (int)(10 - (10 * (temperature - baseTemp) /(1.0*(topTemp - baseTemp))));


        } catch (Exception e) {
            log.warn("\n++++++++++\nTemperature error!" +
                                       "\n temperature = " + temperature + " " +
                                       "\n baseTemp= " + baseTemp +
                                       "\n topTemp = " + topTemp+ "\n++++++++++\n");
            e.printStackTrace();

            return "unknown";
        }

        var starClass = map.get(baseTemp) ;


        log.info("Temperature gives = {} : Class = {} from temp = {} and top Temp = {}",temperature, starClass + deciNumber, baseTemp, topTemp);
        return starClass + deciNumber;
    }
}
