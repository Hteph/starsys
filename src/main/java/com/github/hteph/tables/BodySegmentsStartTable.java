package com.github.hteph.tables;

import com.github.hteph.repository.objects.BodySegment;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.SegmentType;
import com.github.hteph.utils.enums.SensorOrgan;

import java.util.List;

public class BodySegmentsStartTable {

    public static void setOrganLocation(Creature creature) {

        List<BodySegment> bodySegements = creature.getBody().getBodyStructure();

        SensorOrgan limbSensorials = SensorOrgan.ALL_IN_BODY_SEGMENTS;
        switch (Dice.d6() - Dice.d6()) {

            case -6:
            case -5:
                creature.addToDescription(" A central body with most of the creatures organs with a specialized eating appendage");
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.HEAD)
                                             .organ("consumer")
                                             .build());
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("brain")
                                             .organ("sensory")
                                             .organ("metabolic")
                                             .build());
                creature.addAttribute(AttributeEnum.ALERTNESS, -1);
                break;
            case -4:
                creature.addToDescription(" A central body with most of the creatures organs excluding sensory organs and with a specialized eating appendage");
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.HEAD)
                                             .organ("consumer")
                                             .build());
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("brain")
                                             .organ("metabolic")
                                             .build());
                creature.addAttribute(AttributeEnum.REFLEXES, -1);
                limbSensorials = SensorOrgan.ALL_IN_LIMBS;
                break;
            case -3:
                creature.addToDescription(" A central body with most of the creatures organs excluding sensory organs and with a specialized eating appendage");
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.HEAD)
                                             .organ("consumer")
                                             .organ("sensory")
                                             .build());
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("brain")
                                             .organ("metabolic")
                                             .build());
                creature.addAttribute(AttributeEnum.REFLEXES, -1);
                break;
            case -2:
                limbSensorials = SensorOrgan.SECONDARY_IN_LIMBS;
                if (Dice.d6(4)) creature.addAttribute(AttributeEnum.REFLEXES, -1);
            case -1:
            case 0:
            case 1:
            case 2:
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.HEAD)
                                             .organ("consumer")
                                             .organ("sensory")
                                             .organ("brain")
                                             .build());
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("metabolic")
                                             .build());
                break;
            case 3:                //Some difference from the original (paper) table
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("consumer")
                                             .organ("sensory")
                                             .organ("brain")
                                             .organ("metabolic")
                                             .build());
                limbSensorials = SensorOrgan.ALL_IN_LIMBS;
                creature.addAttribute(AttributeEnum.REFLEXES, -1);
                break;
            case 4:
            case 5:
            case 6:
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("consumer")
                                             .organ("sensory")
                                             .organ("brain")
                                             .organ("metabolic")
                                             .build());
                creature.addAttribute(AttributeEnum.ALERTNESS, -1);
                break;
        }
        creature.getBody().setSensorOrgan(limbSensorials);
    }
}