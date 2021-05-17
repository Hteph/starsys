package com.github.hteph.utils;

import java.util.Arrays;

public final class NumberUtilities {

    // Constructor ----------------------------------------------

    private NumberUtilities() {

        //No instances of this class, please
    }

    //Methods --------------------------------------------------

    public static double nicefyDouble(double number) {


        if (number < 0) number = (Math.round(number * 1000)) / 1000.0;
        else if (number < 100) number = (Math.round(number * 100)) / 100.0;
        else number = (Math.pow(10, Math.log10(number) - 2) * (int) number / Math.pow(10, Math.log10(number) - 2));

        return number;
    }

    public static Object chooseFromVector(Object[] arrayOfThings, int[] distArray, int diceRoll) {

        int retVal = Arrays.binarySearch(distArray, diceRoll);

        if (retVal < 0) retVal = -retVal - 2;

        return arrayOfThings[retVal];

    }

    public static double squared(Double number) {

        return number != null ? number * number : 0.0;
    }

    public static double cubed(Double number) {

        return number != null ? number * number * number: 0.0;
    }

    public static double sqrt(Double number) {

        return number != null && number>0 ? Math.sqrt(number) : 0.0;
    }
}
