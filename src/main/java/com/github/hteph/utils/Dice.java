package com.github.hteph.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Dice {

    public static int d6() {

        return (int) (1 + Math.random() * 6);
    }
    public static int d3() {

        return (int) (1 + Math.random() * 3);
    }

    public static boolean d6(int lessThan) {

        return d6() < lessThan;
    }

    public static int _2d6() {

        return d6() + d6();
    }

    public static boolean _2d6(int lessThan) {

        return (d6() + d6()) < lessThan;
    }

    public static int _3d6() {

        return d6() + d6() + d6();
    }

    public static int d10() {
        return (int) (1 + Math.random() * 10);
    }

	public static boolean betweenOrEqual3d6(int lower, int upper){

        //includinc the limits
		int test = d6()+d6()+d6();
		return test-1 > lower && test < upper+1;
	}

    public static boolean _3d6(int lessThan) {

        return d6() + d6() + d6() < lessThan;
    }

    public static boolean _3d6HigherOr(int higherOr) {

        return higherOr < d6() + d6() + d6();
    }

    public static int d20() {
        return (int) (1 + Math.random() * 20);
    }

    public static int aLotOfd3(int count){
        int result = 0;
        for(int i = 0;i<count;i++){
            result +=d3();
        }
        return result;
    }
}
