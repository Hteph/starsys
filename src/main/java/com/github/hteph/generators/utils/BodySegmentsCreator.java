package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.BodySegment;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.SegmentType;
import com.github.hteph.utils.enums.SensorOrgan;

import java.util.ArrayList;
import java.util.List;

public class BodySegmentsCreator {

    public static void make(Creature creature) {

        List<BodySegment> bodySegements = new ArrayList<>();
        SensorOrgan limbSensorials = SensorOrgan.ALL_IN_BODY_SEGMENTS;
        creature.getBody().setBodyStructure(bodySegements);

        var flux = Dice.d6() - Dice.d6();

        switch (flux) {

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
            case 2:
                bodySegements.add(BodySegment.builder()
                                             .segmentType(SegmentType.FRONT)
                                             .organ("consumer")
                                             .organ("sensory")
                                             .organ("brain")
                                             .organ("metabolic")
                                             .build());
                creature.addAttribute(AttributeEnum.ALERTNESS, -1);
                break;
            case 3:
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

//Limbs
        var locomotion = creature.getAttributes()
                                 .keySet()
                                 .stream()
                                 .filter(key -> key.equalsIgnoreCase("Amphibious")
                                         || key.equalsIgnoreCase("Flier")
                                         || key.equalsIgnoreCase("Swimmer"))
                                 .findAny().orElse("Walker");

        List<String> frontSegments = new ArrayList<>();

        String segment="";
        do {
            switch (locomotion) {
                case "Amphibious":
                   // break;
                case "Flier":
                  //  break;
                case "Swimmer":
                 //   break;

                default:
                    segment = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -1, 2, 5},
                            new String[]{"arm+", "arm", "leg", "arm", "arm+"});
            }
            frontSegments.add(segment);
        }while(segment.contains("+"));

        List<String> midSegments = new ArrayList<>();

        do {
            switch (locomotion) {
                case "Amphibious":
                  //  break;
                case "Flier":
                  //  break;
                case "Swimmer":
                  //  break;

                default:
                    int bonus = (int) NumberUtilities.squared(creature.getHomeworld().getGravity());
                    segment = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6()+bonus,
                            new int[]{-6, -3, 3},
                            new String[]{"leg+", "leg", "leg+"});
            }
            midSegments.add(segment);
        }while(segment.contains("+"));


    }
}
