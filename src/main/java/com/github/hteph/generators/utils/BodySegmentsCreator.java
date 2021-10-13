package com.github.hteph.generators.utils;

import com.github.hteph.repository.objects.BodySegment;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.repository.objects.Limbs;
import com.github.hteph.tables.BodySegmentsStartTable;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.LimbType;
import com.github.hteph.utils.enums.SegmentType;

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



        String segment="";
        do {
            switch (locomotionType) {
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
            bodySegements.get(bodySegements.size()-1).setLimbs(getLimb(segment));
            if(segment.contains("+")){
               bodySegements.add(BodySegment.builder()
                                            .segmentType(SegmentType.FRONT)
                                            .organ("metabolic")
                                            .build()) ;
            }

        }while(segment.contains("+"));

        List<String> midSegments = new ArrayList<>();

        do {
            switch (locomotionType) {
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

        }while(segment.contains("+"));


    }

    private static Limbs getLimb(String segment) {

        var limb = Limbs.builder()
                .limbType(LimbType.valueOfLabel(segment.replace("+","")));

        return limb.build();
    }

}
