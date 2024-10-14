package com.github.hteph.starsys.service.tables;

import com.github.hteph.starsys.enums.*;
import com.github.hteph.starsys.service.objects.Creature;
import com.github.hteph.utils.Dice;

import static com.github.hteph.starsys.enums.LocomotionModes.*;

public class EnvironmentalAttributesTable {
    public static void environmentalAttributes(Creature lifeform, EnvironmentalEnum[] environ) {
        HydrosphereDescription liquidStatus;
        LocomotionModes[] walkModes;
        LocomotionModes locomotion;

        var home = lifeform.getHomeworld();

        liquidStatus = home.getHydrosphereDescription();

        lifeform.setClimate(ClimatePref.WARM);

        switch (environ[0]) {
            case COASTAL:
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 10, 13, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                if (lifeform.hasAttribute(WALKER.getName())) {
                    lifeform.addToDescription(" Seeks food in the coastal land. ");
                    DietTable.findTrophicLevel(lifeform, environ[1]);
                } else if (Dice.d6(5) && !lifeform.hasAttribute(SWIMMER.getName())) {
                    DietTable.findTrophicLevel(lifeform, environ[1]);
                    lifeform.addToDescription(" Seeks food in the coastal land. ");
                } else {
                    lifeform.addToDescription(" Seeks food in the coastal waters. ");
                    DietTable.findTrophicLevel(lifeform, environ[0]);
                }
                break;
            case ALPINE:
                walkModes = new LocomotionModes[]{JUMPER, GLIDER, FLIER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 6, 9, 17},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addToDescription("Evolved high up in mountain ranges. ");
                DietTable.findTrophicLevel(lifeform, environ[1]);
                break;
            case TUNDRA:
                lifeform.addToDescription("Lived on frozen tundra or ice shelf. ");
                lifeform.setClimate(ClimatePref.COLD);
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, BURROWER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 6, 9, 17},
                        walkModes);
                lifeform.addAttribute(locomotion);
                if (locomotion.equals(WALKER)) {
                    int bonus = 0;
                    if (liquidStatus == HydrosphereDescription.CRUSTAL || liquidStatus == HydrosphereDescription.ICE_SHEET) {
                        bonus += 5;
                    }
                    if (Dice._3d6(4 + bonus)) {
                        lifeform.addAttribute("Skater", "Specialised for moving fast and efficient on ice surfaces. ");
                    }
                }
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case MIRES:
                lifeform.addToDescription("Evolved in wetlands");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 6},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT}));
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 9, 11, 17},
                        walkModes
                );
                lifeform.addAttribute(locomotion);
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case CONIFEROUS_FORESTS:
                lifeform.addToDescription("Evolved in a coniferous forest. ");
                if (Dice.d6(6)) {
                    lifeform.setClimate(ClimatePref.COLD);
                }
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, BURROWER, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 4, 5, 6, 8, 10, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a plant rich environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case TEMPERATE_FORESTS:
                lifeform.addToDescription("Evolved in a forest biome. ");
                if (Dice.d6(2)) {
                    lifeform.setClimate(ClimatePref.COLD);
                }
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, BURROWER, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 4, 5, 6, 8, 10, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a large plant environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case GRASSLANDS:
                lifeform.addToDescription("Evolved on a bountiful plain. ");
                if (Dice.d6(3)) {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case HEATHLANDS:
                lifeform.addToDescription("Evolved on ha harsh plain. ");
                if (Dice.d6(2)) {
                    lifeform.setClimate(ClimatePref.COLD);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case SHRUBLANDS:
                lifeform.addToDescription("Evolved on a plain with low sturdy vegetation. ");
                if (Dice.d6(2)) {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case DESERT:
                lifeform.addToDescription(" Evolved in a desert environment. ");
                lifeform.setClimate(ClimatePref.HOT);
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 9, 11, 17},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Desert dweller", "This species body evolved for surviving hot and dry climates ");
                if (Dice.d6(3)) {
                    lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                }
                if (Dice.d6(3)) {
                    lifeform.addAttribute("Nictating Membrane",
                                          "The lifeform have a transparent lens over the eyes that you can open and close like an eyelid. This protects the eyes from irritants. ");
                    lifeform.addAttribute(AttributeEnum.VISION);
                }
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case TEMPERATE_AND_SEMI_DESERTS:
                lifeform.addToDescription("Evolved in a dry desertlike climate");
                if (Dice.d6(3)) {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 7, 9, 17},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Desert dweller", "This species body evolved for surviving hot and dry climates. ");
                if (Dice.d6(3)) {
                    lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                }
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case TROPICAL_SAVANNAS:
                lifeform.addToDescription("Evolved on a hot and rich plain. ");
                lifeform.setClimate(ClimatePref.HOT);
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case RAIN_FOREST:
                lifeform.addToDescription("Evolved in a rainforest. ");
                if (Dice._2d6(10)) {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, BURROWER, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{4, 5, 6, 9, 10, 15, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a plant rich environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case WETLAND_FORESTS:
                lifeform.addToDescription("Evolved in a wetland forest. ");
                if (Dice._2d6(9)) {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, AMPHIBIOUS, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{4, 5, 6, 9, 10, 15, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a plant rich environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case MANAGED_GRASSLANDS:
                lifeform.addToDescription("Evolved in a managed system. ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                if (Dice._2d6(6)) {
                    lifeform.setClimate(ClimatePref.WARM);
                } else {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case FIELD_CROP:
                lifeform.addToDescription("Evolved in a managed system. ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                if (Dice._2d6(10)) {
                    lifeform.setClimate(ClimatePref.WARM);
                } else {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{BURROWER, JUMPER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 9, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Plains dweller", "This species body evolved for living in open areas. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case TREE_CROP:
                lifeform.addToDescription("Evolved in a managed system. ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                lifeform.addToDescription("Evolved in a cultivated forest. ");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 6},
                        new ClimatePref[]{ClimatePref.COLD, ClimatePref.WARM, ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, BURROWER, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{4, 5, 6, 9, 10, 15, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a plant rich environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case GREENHOUSE:
                lifeform.addToDescription("Evolved inside an artifical bio system ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                if (Dice._2d6(4)) {
                    lifeform.setClimate(ClimatePref.WARM);
                } else {
                    lifeform.setClimate(ClimatePref.HOT);
                }
                walkModes = new LocomotionModes[]{CLINGER, GLIDER, BRACHIATOR, BURROWER, CLIMBER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{4, 5, 6, 9, 10, 15, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                lifeform.addAttribute("Forest dweller", "This species evolved for living in a plant rich environment. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case BIOINDUSTRIAL:
                lifeform.addToDescription("Evolved inside an artifical bio system ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 6},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AQUATIC, SWIMMER, BURROWER, BRACHIATOR, CLINGER, CLIMBER, GLIDER, JUMPER, AMPHIBIOUS, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case RIVER_AND_STREAM:
                lifeform.addToDescription("The lifeform evolved in streams or rivers. ");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 5},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 10, 13, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);

                if (Dice.d6(4) && !lifeform.getAttributes().containsKey("Aquatic")) {
                    DietTable.findTrophicLevel(lifeform, environ[1]);
                    lifeform.addToDescription("Mainly search for food in terrain around the water. ");
                } else {
                    DietTable.findTrophicLevel(lifeform, environ[0]);
                }
                break;
            case LAKES:
                lifeform.addToDescription("Evolved in lakes");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 5},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, WALKER, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 10, 13, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);

                if (Dice.d6(4) && !lifeform.hasAttribute(AQUATIC.getName())) {
                    DietTable.findTrophicLevel(lifeform, environ[1]);
                    lifeform.addToDescription("Mainly search for food in terrain around the water. ");
                } else {
                    DietTable.findTrophicLevel(lifeform, environ[0]);
                }
                break;
            case INTERTIDAL_AND_LITTORAL:
                lifeform.addToDescription("Evolved in a tidal or near coastal environment");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 6},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, AQUATIC, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 10, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case REEFS:
                lifeform.addToDescription("Evolved at a bountiful reef. ");
                lifeform.addAttribute("Aquatic", "Obligat living in water and can't survive on dry land. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case ESTUARIES:
                lifeform.addToDescription("Evolved where rivers meets the sea");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 2, 6},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, AQUATIC, FLIER};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 10, 18},
                        walkModes);
                lifeform.addAttribute(locomotion);

                if (Dice._2d6(3) && !lifeform.hasAttribute(AQUATIC.getName())) {
                    DietTable.findTrophicLevel(lifeform, environ[1]);
                    lifeform.addToDescription("Mainly search for food in terrain around the water. ");
                } else {
                    DietTable.findTrophicLevel(lifeform, environ[0]);
                }

                break;
            case SHELVES:
                lifeform.addToDescription("Evolved at the continal plates edge to the ocean. ");
                lifeform.addAttribute("Aquatic", "Obligat living in water and can't survive on dry land. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case DEEP_OCEAN:
                lifeform.addToDescription("Evolved as a denizen of the free ocean. ");
                lifeform.addAttribute("Aquatic", "Obligat living in water and can't survive on dry land. ");
                if (Dice.d6(3)) {
                    lifeform.addAttribute("Pressure Support", "The species can regulate its internal pressure and survive very high water pressures");
                    if (Dice.d6(3)) {
                        lifeform.addAttribute("Thermal Vent", "The species can survive the super heated temperatures of sub oceanic vulcanism");
                    }
                }
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case MANAGED_AQUATIC:
                lifeform.addToDescription("Evolved in a managed system. ");
                lifeform.addAttribute("Aquatic", "Obligat living in water and can't survive on dry land. ");
                lifeform.addAttribute("Artifical Ecosystem", "The lifeform evolved in a biome created by another lifeform, either on purpose, accident or invasive. ");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case CAVE:
                lifeform.addToDescription("Evolved in cave systems");
                lifeform.setClimate(TableMaker.makeRoll(
                        Dice.d6(),
                        new int[]{1, 3, 5},
                        new ClimatePref[]{ClimatePref.COLD,
                                ClimatePref.WARM,
                                ClimatePref.HOT})
                );
                walkModes = new LocomotionModes[]{AMPHIBIOUS, SWIMMER, WALKER, AQUATIC};
                locomotion = TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 8, 10, 16},
                        walkModes);
                lifeform.addAttribute(locomotion);
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case EXOTIC:
                //Just a placeholder
                lifeform.addAttribute("Flier", "Uses wings or equivalent to get airborne");
                DietTable.findTrophicLevel(lifeform, environ[0]);
                break;
            case NONE:
                break;
            default:
                break;
        }

        if (lifeform.hasAttribute(AMPHIBIOUS.getName())) {
            lifeform.addAttribute(AttributeEnum.DEPENDENCY).addCondition("Water", "Lifeform needs immersion in water daily");
            if (Dice.d6(4)) {
                lifeform.addAttribute("Breath Holding", 2, "By gathering air and store it internaly this species operates underwater for extended time");
            } else if (Dice._3d6(5)) {
                lifeform.addAttribute("Oxygen Storage", 2,
                                      "This species is able through metabolic processes store oxygen for extended operations under water");
            } else {
                lifeform.addAttribute("Gills", "This species has an organ that is specialised to filter oxygen from water. ");
            }
        }

        if (lifeform.hasAttribute(LocomotionModes.BRACHIATOR.getName())) {
            lifeform.addAttribute(AttributeEnum.REFLEXES, 1);
            lifeform.addAttribute(AttributeEnum.STRENGTH, 2)
                    .addCondition("Swinging Limbs", "This lifeform has strong bodyStructure specialised for swinging. ");
        }

        if (lifeform.hasAttribute(LocomotionModes.CLIMBER.getName())) {
            lifeform.addAttribute(AttributeEnum.AGILITY, 2);
            if (Dice._3d6(8)) {
                lifeform.addAttribute("Perfect Balance", "The lifeforms kinestetics allows them to always keep their footing, no matter how narrow the walking surface. ");
            }
        }

        if (lifeform.hasAttribute(SWIMMER.getName())) {
            lifeform.addAttribute(AttributeEnum.DEPENDENCY).addCondition("Water", "Lifeform needs immersion in water daily");
            if (Dice.d6(5)) {
                lifeform.addAttribute("Breath Holding", "By gathering air and store it internaly this species operates underwater for extended time");
            } else if (Dice._3d6(5)) {
                lifeform.addAttribute("Oxygen Storage",
                                      "This species is able through metabolic processes store oxygen for extended operations under water");
            }
        }

        if (lifeform.hasAttribute(BURROWER.getName())) {
            lifeform.addAttribute("Claws", "The lifeforms bodyStructure has devolped harden parts to cause damage. ");
            if (Dice._3d6(8)) {
                lifeform.addAttribute("Nictating Membrane",
                                      "The lifeform have a transparent lens over the eyes that you can open and close like an eyelid. This protects the eyes from irritants. ");
            }
            if (Dice._3d6(8)) {
                lifeform.addAttribute(AttributeEnum.STRENGTH);
            }
            lifeform.addAttribute("Tunnel Dweller", "The lifeform evolved in selfbuilt tunnel systems. ");
            lifeform.addAttribute("Phobia", "Agoraphobia, mild");
        }

        if (lifeform.hasAttribute(AQUATIC.getName())) {
            lifeform.addAttribute(AttributeEnum.DEPENDENCY).addCondition("Water", "Lifeform needs immersion in water to survive. ");
            if (Dice.d6(2)) {
                lifeform.addAttribute("Breath Holding", 2, "By gathering air and store it internaly this species operates underwater for extended time");
            } else if (Dice._3d6(5)) {
                lifeform.addAttribute("Oxygen Storage", 2,
                                      "This species is able through metabolic processes store oxygen for extended operations under water");
            } else {
                lifeform.addAttribute("Gills", "This species has an organ that is specialised to filter oxygen from water. ");
            }
            if (Dice._3d6(6)) {
                lifeform.addAttribute("Sonar Vision", "The lifeform uses sound to paint a picture of the surrounding. ");
            } else if (Dice._3d6(8)) {
                lifeform.addAttribute("Faz Sense", "Can sense weak electric currents. ");
            }
        }

        if (lifeform.hasAttribute(GLIDER.getName())) {
            if (Dice._3d6(8)) {
                lifeform.addAttribute(AttributeEnum.AGILITY);
            }
            lifeform.addAttribute("Flight", "The lifeform can take to the air. ")
                    .addCondition("Gliding", "Only gliding flight");
            if (Dice._3d6(8)) {
                lifeform.addAttribute("Phobia", "Claustrophobia, mild");
            }
            if (Dice._3d6(10)) {
                lifeform.addAttribute("Fragile", "The lifeform has hollow bones or otherwise weight reducing bodybuild. ");
                lifeform.addAttribute(AttributeEnum.CONSTITUTION, -1);
            }
        }

        if (lifeform.hasAttribute(FLIER.getName())) {

            int bonus = 0;
            if ((lifeform.getClimate().equals(ClimatePref.COLD)
                 || lifeform.getHabitat().equals(EnvironmentalEnum.INTERTIDAL_AND_LITTORAL))) {
                bonus = 1;
            }
            if (Dice._2d6(2 + bonus)) {
                lifeform.addAttribute("Fligth", "The lifeform can take to the air. ")
                        .addCondition("Balloon", "Only ligther than air flight");
            } else {
                lifeform.addAttribute("Fligth", "The lifeform can take to the air. ");
            }

            if (Dice._3d6(9)) {
                lifeform.addAttribute("Absolute Direction", "The lifeform has developed an excellent sense of direction and navigation. ");
            }
            if (Dice._3d6(8)) {
                lifeform.addAttribute(AttributeEnum.AGILITY);
            }
            if (Dice._3d6(10)) {
                lifeform.addAttribute("Phobia", "claustrophobia, mild");
            }
            if (Dice._3d6(10)) {
                lifeform.addAttribute("Fragile", "The lifeform has hollow bones or other weight reducing bodybuild. ");
                lifeform.addAttribute(AttributeEnum.CONSTITUTION, -1);
            }

            if (lifeform.getAttributes().containsKey(FLIER.getName().toLowerCase())
                && lifeform.getAttributes().get(FLIER.getName().toLowerCase()).hasCondition("Balloon")) {
                if (Dice._3d6(12)) {
                    lifeform.addAttribute("Phobia", "Sharp objects, mild. ");
                }
            } else {
                lifeform.addAttribute(AttributeEnum.REFLEXES, 1);
            }
        }
    }
}