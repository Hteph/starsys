package com.github.hteph.generators;

import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.wrappers.Homeworld;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreatureGeneratorTest {

    @Test
    void TheCreatureGenerator() {

        var creature = CreatureGenerator.generator(Biosphere.builder()
                                             .homeworld(Homeworld.builder()
                                                                 .gravity(1.0)
                                                                 .build())
                                             .build());

        assertThat(creature.getBody().getBodyStructure()).isNotNull();
    }
}