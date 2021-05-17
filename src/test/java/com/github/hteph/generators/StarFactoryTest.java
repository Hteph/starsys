package com.github.hteph.generators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StarFactoryTest {

    @Test
    public void testStarGeneration(){
        var star = StarFactory.get("Test",'A',null);

        assertThat(star).isNotNull();
    }

}