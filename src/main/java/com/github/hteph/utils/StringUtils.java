package com.github.hteph.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String nicefyGasName(String name){

        final String SUBSCRIPT_TWO = "\u2082";
        final String SUBSCRIPT_THREE = "\u2083";
        final String SUBSCRIPT_FOUR = "\u2084";

        return name.replace("2", SUBSCRIPT_TWO)
                .replace("3",SUBSCRIPT_THREE)
                .replace("4",SUBSCRIPT_FOUR);
        }

        public static String nicefyName(String name){

        return name.substring(0,1).toUpperCase()+name.substring(1);

        }

}
