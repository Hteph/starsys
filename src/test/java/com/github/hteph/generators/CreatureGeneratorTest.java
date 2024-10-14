package com.github.hteph.generators;

import com.github.hteph.starsys.service.objects.Biosphere;
import com.github.hteph.starsys.service.generators.CreatureGenerator;
import com.github.hteph.starsys.service.objects.wrappers.Homeworld;
import com.github.hteph.starsys.enums.BaseElementOfLife;
import com.github.hteph.starsys.enums.Breathing;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreatureGeneratorTest {

    @Test
    void TheCreatureGenerator() {

        var creature = CreatureGenerator.generator(Biosphere.builder()
                                                            .respiration(Breathing.OXYGEN)
                                                            .baseElement(BaseElementOfLife.CARBON)
                                                            .homeworld(Homeworld.builder()
                                                                                .name("TestName")
                                                                                .gravity(1.0)
                                                                                .build())
                                                            .build());

        assertThat(creature.getBody().getBodyStructure()).isNotNull();
    }
}