package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.BodySegment;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.repository.objects.CreatureBody;
import com.github.hteph.repository.objects.Limbs;
import com.github.hteph.tables.BodySegmentsStartTable;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.LimbType;
import com.github.hteph.utils.enums.SegmentType;
import com.github.hteph.utils.enums.Symmetry;

import java.util.ArrayList;
import java.util.List;

public class BodySegmentsCreator {

    public static void make(Creature creature) {

        List<BodySegment> bodySegements = new ArrayList<>();

        creature.getBody().setBodyStructure(bodySegements);

        BodySegmentsStartTable.setOrganLocation(creature);

//Limbs
        var locomotionType = creature.getAttributes()
                                 .keySet()
                                 .stream()
                                 .filter(key -> key.equalsIgnoreCase("Amphibious")
                                         || key.equalsIgnoreCase("Flier")
                                         || key.equalsIgnoreCase("Swimmer"))
                                 .findAny().orElse("Walker");



        String limb;
        do {
            switch (locomotionType) {
                case "Amphibious":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -1, 2},
                            new String[]{"arm+", "arm", "leg", "fin"});
                   break;
                case "Flier":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -1, 2, 4  },
                            new String[]{"arm+", "arm", "wing", "wing+", "leg"});
                    break;
                case "Swimmer":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -3, -2, -1},
                            new String[]{"arm+", "arm", "leg", "fin"});
                    break;

                default:
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -1, 2, 5},
                            new String[]{"arm+", "arm", "leg", "arm", "arm+"});
            }
            bodySegements.get(bodySegements.size()-1).setLimbs(getLimb(limb));
            if(limb.contains("+")){
               bodySegements.add(BodySegment.builder()
                                            .segmentType(SegmentType.FRONT)
                                            .organ("metabolic")
                                            .build()) ;
            }

        }while(limb.contains("+"));

        boolean createSegment = creature.getBody().getBodySymmetry() != Symmetry.NONE && Dice.d6(4);

        while (createSegment) {

            switch (locomotionType) {
                case "Amphibious":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -2, 3},
                            new String[]{"leg+", "leg", "fin", "fin+"});
                    break;
                case "Flier":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -3, 4, 5},
                            new String[]{"wing", "leg", "arm", "arm+"});
                    break;
                case "Swimmer":
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6(),
                            new int[]{-6, -4, -2},
                            new String[]{"fin+", "fin", "none"});
                  break;

                default:
                    int bonus = (int) NumberUtilities.squared(creature.getHomeworld().getGravity());
                    limb = TableMaker.makeRoll(
                            Dice.d6() - Dice.d6() + bonus,
                            new int[]{-6, -3, 3},
                            new String[]{"leg+", "leg", "leg+"});
            }

            bodySegements.add(BodySegment.builder()
                                         .segmentType(SegmentType.MID)
                                         .limbs(getLimb(limb))
                                         .organ("metabolic")
                                         .build());

            createSegment = limb.contains("+");
        };

    }

    public static void setOtherBodySymmetry(Creature lifeform, CreatureBody.CreatureBodyBuilder bodyBuilder) {
        int sides = Dice.d6() + 2;
        switch (sides) {
            case 3:
                bodyBuilder.bodySymmetry(Symmetry.TRILATERAL);
                lifeform.addToDescription("Trilateral body symmetry. ");
                bodyBuilder.limbPerSegment(3*getMultiple(new int[]{2, 10, 11}));
                break;
            case 4:
                bodyBuilder.bodySymmetry(Symmetry.QUADRAL);
                lifeform.addToDescription("Quadratic body symmetry. ");
                bodyBuilder.limbPerSegment(4* getMultiple(new int[]{2, 11, 12}));
                break;
            case 5:
                bodyBuilder.bodySymmetry(Symmetry.PENTRADAL);
                lifeform.addToDescription("Pentagonal body symmetry. ");
                bodyBuilder.limbPerSegment(5*getMultiple(new int[]{2, 11, 12}));
                break;
            default:
                bodyBuilder.bodySymmetry(Symmetry.RADIAL);
                lifeform.addToDescription("Radial body symmetry. ");
                bodyBuilder.limbPerSegment(2 + Dice.d3()*Dice.d6());
                break;
        }
    }

    private static Limbs getLimb(String segment) {

        var limb = Limbs.builder()
                .limbType(LimbType.valueOfLabel(segment.replace("+","")));

        return limb.build();
    }

    private static Integer getMultiple( int[] distribution) {

        return TableMaker.makeRoll(
                Dice._2d6(),
                distribution,
                new Integer[]{1, 2, Dice.d6() + 2});
    }

}
