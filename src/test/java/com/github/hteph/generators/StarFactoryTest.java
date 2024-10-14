package com.github.hteph.generators;

import com.github.hteph.starsys.service.generators.StarFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StarFactoryTest {

    @Test
    public void testStarGeneration(){
        var star = StarFactory.get("Test", 'A', null);

        assertThat(star).isNotNull();
    }

}