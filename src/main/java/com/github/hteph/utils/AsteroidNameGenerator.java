package com.github.hteph.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class AsteroidNameGenerator {

    public String compose() {
//Scientific garble is generated until AWS supports coretto 17 servers in Elastic Beanstalk
        Random rand = new Random();

        StringBuilder name = new StringBuilder();

        do {
            String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            char letter = abc.charAt(rand.nextInt(abc.length()));

            name.append(letter);
        } while (name.length() <4);

        name.append("-");
        name.append(rand.nextInt(999));

        return name.toString();
    }
}
