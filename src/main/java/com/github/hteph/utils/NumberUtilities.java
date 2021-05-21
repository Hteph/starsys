package com.github.hteph.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.MathContext;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtilities {

    public static final MathContext TWO = new MathContext(2);
    public static final MathContext THREE = new MathContext(3);
    public static final MathContext FOUR = new MathContext(4);
    public static final MathContext FIVE = new MathContext(5);

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
