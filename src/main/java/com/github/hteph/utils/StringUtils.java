package com.github.hteph.utils;

public class StringUtils {

    public static String nicefyGasName(String name){

        String SUBSCRIPT_TWO = "\u2082";
        String SUBSCRIPT_THREE = "\u2083";
        String SUBSCRIPT_FOUR = "\u2084";

        return name.replace("2", SUBSCRIPT_TWO)
                .replace("3",SUBSCRIPT_THREE)
                .replace("4",SUBSCRIPT_FOUR);
        }

}
