package com.github.hteph.generators.utils;

import com.github.hteph.starsys.service.objects.Creature;
import com.github.hteph.starsys.service.objects.CreatureBody;
import com.github.hteph.starsys.service.generators.utils.BodySegmentsCreator;
import com.github.hteph.starsys.service.objects.wrappers.Homeworld;
import com.github.hteph.starsys.enums.Symmetry;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class BodySegmentsCreatorTest {

    @Test
    void testMakeStructure() {

        var creature = Creature.builder()
                               .attributes(new HashMap<>())
                               .homeworld(Homeworld.builder()
                                                   .gravity(1.0)
                                                   .build())
                               .body(CreatureBody.builder()
                                                 .bodySymmetry(Symmetry.BILATERAL)
                                                 .build())
                               .build();

        BodySegmentsCreator.make(creature);

        assertThat(creature.getBody().getBodyStructure()).isNotNull();
    }
}