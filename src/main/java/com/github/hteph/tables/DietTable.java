package com.github.hteph.tables;

import com.github.hteph.repository.objects.Creature;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.TrophicLevels;

import java.util.TreeMap;

import static com.github.hteph.utils.enums.TrophicLevels.*;

public class DietTable {

    public static void findTrophicLevel(Creature lifeform, EnvironmentalEnum environ) {

        int bonus = 0;
        //TODO switch the random calc to dice based

        final TrophicLevels[] TROPHIC_LEVELS = new TrophicLevels[]{CARNIVORE, OMNIVORE, HERBIVORE, AUTOTROPH};

        switch (environ) {
            case COASTAL:
            case RIVER_AND_STREAM:
            case LAKES:
            case ESTUARIES:
            case REEFS:
            case INTERTIDAL_AND_LITTORAL:
                if (lifeform.hasAttribute("Flier")) bonus += -1;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 13, 15, 18},
                        TROPHIC_LEVELS)
                );
                break;
            case ALPINE:
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{1, 11, 14, 18},
                        TROPHIC_LEVELS)
                );
                break;
            case TUNDRA:
                if (lifeform.hasAttribute("Swimmer") || lifeform.hasAttribute("Amphibious")) bonus += -2;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 11, 14, 18},
                        TROPHIC_LEVELS)
                );
                break;
            case MIRES:
            case WETLAND_FORESTS:
                if (lifeform.hasAttribute("Flier")) bonus += -1;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 9, 11, 17},
                        TROPHIC_LEVELS)
                );
                break;
            case TEMPERATE_FORESTS:
            case RAIN_FOREST:
            case CONIFEROUS_FORESTS:
                if (lifeform.hasAttribute("Flier")) bonus += -2;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 9, 12, 17},
                        TROPHIC_LEVELS)
                );
                break;
            case GRASSLANDS:
            case TROPICAL_SAVANNAS:
            case HEATHLANDS:
            case SHRUBLANDS:
                if (lifeform.hasAttribute("Flier")) bonus += -3;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 10, 11, 17},
                        TROPHIC_LEVELS)
                );
                break;
            case DESERT:
            case TEMPERATE_AND_SEMI_DESERTS:
                if (lifeform.hasAttribute("Swimmer") || lifeform.hasAttribute("Amphibious")) bonus += -3;
                if (lifeform.hasAttribute("Burrower")) bonus += 1;
                if (lifeform.hasAttribute("Flier")) bonus += -3;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{3, 10, 14, 15},
                        TROPHIC_LEVELS)
                );
                break;
            case BIOINDUSTRIAL:
            case GREENHOUSE:
            case TREE_CROP:
            case FIELD_CROP:
            case MANAGED_GRASSLANDS:
            case MANAGED_AQUATIC:
            case CAVE:
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6(),
                        new int[]{3, 10, 14, 15},
                        TROPHIC_LEVELS)
                );

                break;

            case DEEP_OCEAN:
            case SHELVES:
                if (lifeform.getAttributes().containsKey("Thermal Vent")) bonus += 3;
                if (lifeform.hasAttribute("Flier")) bonus += -1;
                lifeform.setThrophicLevel(TableMaker.makeRoll(
                        Dice._3d6() + bonus,
                        new int[]{1, 13, 15, 18},
                        TROPHIC_LEVELS)
                );
                break;

            case EXOTIC:
                lifeform.setThrophicLevel(AUTOTROPH);
                break;
            default:
                lifeform.setThrophicLevel(OMNIVORE);
                break;
        }

        switch (lifeform.getThrophicLevel()) {
            case CARNIVORE:
                findCarnivoreSpeciality(lifeform);
                break;
            case OMNIVORE:
                findOmnivoreSpeciality(lifeform);
                break;
            case AUTOTROPH:
                findErgivoreSpeciality(lifeform);
                break;
            default:
                findHerbivoreSpeciality(lifeform);
                break;
        }

    }

    private static void findHerbivoreSpeciality(Creature lifeform) {

        int bonus = 0;
        if (lifeform.hasAttribute("Aquatic")) bonus += 1;
        if (lifeform.hasAttribute("Swimmer")) bonus += 1;
        if (lifeform.hasAttribute("Plains Dweller")) bonus -= 2;
        if (lifeform.hasAttribute("Forest Dweller")) bonus += 1;

        String speciality = TableMaker.makeRoll(
                Dice._2d6() + bonus,
                new int[]{0, 5, 8, 9},
                new String[]{"Grazing", "Browsing", "Saprophytic", "Gatherers"}
        );
        switch (speciality) {
            case "Gathering":
                lifeform.addAttribute("Gatherers", "Eat high-energy fruits, nuts and seeds, plus insects. Of the herbivores, they spend the least time eating.");
                if (Dice.betweenOrEqual3d6(6, 7)) lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                if (Dice._3d6(7)) lifeform.addAttribute(AttributeEnum.CURIOSITY);
                if (Dice._3d6(7)) lifeform.addAttribute(AttributeEnum.IMAGINATION);
                if (Dice._3d6(7)) lifeform.addAttribute(AttributeEnum.VISION);
                if (Dice._3d6(9)) lifeform.addAttribute(AttributeEnum.TASTE);
                break;
            case "Saprophytic":
                lifeform.addAttribute("Saprophytic", "A creature who live off of the chemical decompositions of rotting flesh and vegetable matter. Saprophytosis yields little energy, and saprophytes spend a great deal of time squatting in piles of carrion and compost. On the positive side, their 'food' need not be of very high quality.");
                lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                lifeform.addAttribute(AttributeEnum.CURIOSITY, -1);
                lifeform.addAttribute(AttributeEnum.IMAGINATION, -1);
                lifeform.addAttribute(AttributeEnum.EMPATHY, -1);
                lifeform.addAttribute("Slow Eater", "Due to how the metabolic tract is constructed or way of feeding the act of eating takes a considerable amount of time.");

                if (Dice._3d6(9)) lifeform.addAttribute(AttributeEnum.CURIOSITY, -1);
                if (Dice._3d6(9)) lifeform.addAttribute(AttributeEnum.LIFESUPPORT, -1);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.SMELL);
                if (Dice.betweenOrEqual3d6(3, 12))
                    lifeform.addAttribute("Cast Iron Stomach", "Can eat or drink almost anything without getting sick.");
                else if (Dice.betweenOrEqual3d6(3, 5))
                    lifeform.addAttribute("Unversial Digestion", "Through exotic metabolism components anything containing the basic atoms of life can be used for food for this species.");
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Sessile", "For the major part of the lifeforms life span it remains attached to the same spot.");
                break;
            case "Browsing":
                lifeform.addAttribute("Browsing", "Consume leaves, shoots, roots and other mid-energy plant food.");
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.IMAGINATION, -1);
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.EMPATHY, -1);
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                if (Dice.betweenOrEqual3d6(3, 6))
                    lifeform.addAttribute("Cast Iron Stomach", "Can eat or drink almost anything without getting sick.");
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.SMELL);
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Slow Eater", "Due to how the metabolic tract is constructed or way of feeding the act of eating takes a considerable amount of time.");
                break;
            case "Grazing":
                lifeform.addAttribute("Grazer", "Spend much of their time eating low-energy food such as grass.");
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.CURIOSITY, -1);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                lifeform.addAttribute(AttributeEnum.IMAGINATION, -1);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.SUSPICION);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.EMPATHY, -1);
                lifeform.addAttribute("Slow Eater", "Due to how the metabolic tract is constructed or way of feeding the act of eating takes a considerable amount of time.");
                break;
        }
    }

    private static void findErgivoreSpeciality(Creature lifeform) {

        int bonus = 0;
        if (lifeform.hasAttribute("Forest Dweller")) bonus += 1;
        if (lifeform.hasAttribute("Thermal Vent")) bonus += 4;
        if (lifeform.hasAttribute("Arctic")) bonus += 1;

        String choice;
        if (lifeform.getHomeworld().getMagneticField() > 5 + Dice.d6()) {
            choice = Dice.d6(4) ? "Mixed" : "Rooted";
        } else {

            choice = TableMaker.makeRoll(
                    Dice._2d6() + bonus,
                    new int[]{0, 5, 7, 11},
                    new String[]{"Pure", "Mixed", "Rooted", "Tapper"}
            );
        }

        switch (choice) {
            case "Tapper":
                lifeform.addAttribute("Tapper", "Tappers wander about seeking high-energy sources. The energy source must be intense, and may be rare in nature (e.g., electrical currents, radioactives, geothermal areas.");
                lifeform.addAttribute(AttributeEnum.CONCENTRATION);
                lifeform.addAttribute(AttributeEnum.IMAGINATION);
                lifeform.addAttribute("Dependency", "The tapper consumes modest amounts of food to obtain minerals and fluids but must tap into an energy source for at least 24 hours once per week.");
                lifeform.addAttribute("Territorial", "The lifeform is dependent on a specific source of food and is protective of that.");
                lifeform.addAttribute(AttributeEnum.LIFESUPPORT, -1);
                lifeform.addAttribute(AttributeEnum.SUSTENANCE, -2);
                if (Dice.betweenOrEqual3d6(3, 12))
                    lifeform.addAttribute("Special Sense", "The lifeform has a special sense dedicated to find its preffered energy type.");
                break;
            case "Rooted":
                lifeform.addAttribute("Rooted Ergivore", "They use energy (e.g., sunlight, microwaves, etc.) to process minerals, water and organic chemicals into food. May be mobile, but are slow.");
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.IMAGINATION, -1);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.SUSPICION, -1);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.CHAUVINISM, -1);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.EMPATHY, -1);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                lifeform.addAttribute(AttributeEnum.LIFESUPPORT, -2);
                lifeform.addAttribute("Slow Eater", "Due to how the metabolic tract is constructed or way of feeding the act of eating takes a considerable amount of time.");
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Injury Tolerance", "The Lifeform have fewer physiological weaknesses than ordinary living beings.")
                            .addCondition("No Vitals", "The Lifeform lacks critical organs that could be damaged.");
                lifeform.addAttribute("Dependency", "Plants have a dependency on energy of 8 hours exposure a day)");
                lifeform.addAttribute("Dependency", "Needs to be rooted at least 4 hours a day to retrive nutrients from the soil.");
                if (Dice.betweenOrEqual3d6(3, 12)) lifeform.addAttribute(AttributeEnum.MOVEMENT, -2);
                else if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Sessile", "For the major part of the lifeforms life span it remains attached to the same spot.");
                break;
            case "Mixed":
                lifeform.addAttribute("Mixed Ergivore", "They depend on energy for only part of their food. In fact, the ergivore part may be a symbiote, supplying nutrition in lean times in exchange for protection and waste products.");
                lifeform.addAttribute("Dependency", "If the mixed ergivore does not receive 6 hours of the prefered energy type per day, it grows weak");
                if (Math.random() < 0.3) findCarnivoreSpeciality(lifeform);
                else if (Math.random() < 0.3) findOmnivoreSpeciality(lifeform);
                else findHerbivoreSpeciality(lifeform);
                lifeform.addAttribute(AttributeEnum.CONSTITUTION, 2);
                lifeform.addAttribute(AttributeEnum.LIFESUPPORT, -1);
                break;
            case "Pure":
                lifeform.addAttribute("Pure Ergivore", "They depend on energy for the life processes. They still need nutrietns and building material for growth and healing, but only a limited amount of non energy sustenance for everyday needs.");

                lifeform.addAttribute(AttributeEnum.IMAGINATION, -1);
                lifeform.addAttribute(AttributeEnum.CONCENTRATION, -1);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.SUSPICION, -1);
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.EMPATHY, -1);
                if (Dice.betweenOrEqual3d6(6, 9)) lifeform.addAttribute(AttributeEnum.CHAUVINISM, -1);
                lifeform.addAttribute("Doesn't Breathing", "The lifeform do not use gas exchange organs like lungs or gills.");
                lifeform.addAttribute(AttributeEnum.LIFESUPPORT, -2);
                if (Dice.betweenOrEqual3d6(3, 10))
                    lifeform.addAttribute("Injury Tolerance", "The Lifeform have fewer physiological weaknesses than ordinary living beings.")
                            .addCondition("No Vitals", "The Lifeform lacks critical organs that could be damaged.");
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Injury Tolerance", "The Lifeform have fewer physiological weaknesses than ordinary living beings.")
                            .addCondition("No Blood", "The Lifeform do not utilize a cardiovascular system in the common sense.");
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Special Sense", "The lifeform has a special sense dedicated to find its preferred energy type.");
                if (Dice.betweenOrEqual3d6(3, 10)) {
                    lifeform.addAttribute(AttributeEnum.SMELL, -5);
                    lifeform.addAttribute(AttributeEnum.TASTE, -5);
                }
                lifeform.addAttribute("Dependency", "The ergivore must get 6 hours of quality irradiation per day to be at full strength.")
                        .addCondition("Nutrients", "A minuscule of water and minerals are necessary for them to grow, heal, and reproduce.");
                lifeform.addAttribute(AttributeEnum.SUSTENANCE, -4);
                break;
        }
    }

    private static void findOmnivoreSpeciality(Creature lifeform) {
        int bonus = 0;
        if (lifeform.hasAttribute("Forest Dweller")) bonus -= 2;
        if (lifeform.hasAttribute("Swimmer")) bonus += 2;
        if (lifeform.hasAttribute("Aquatic")) bonus += 2;
        if (lifeform.hasAttribute("Amphibian")) bonus += 2;
        if (lifeform.getHabitat().equals(EnvironmentalEnum.MIRES)) bonus -= 2;

        String choice = TableMaker.makeRoll(
                Dice._2d6() + bonus,
                new int[]{0, 3, 5},
                new String[]{"Gatherer/Hunter", "Opportunist Browser", "Hunter/Browser"}
        );
        switch (choice) {
            case "Hunter/Browser":
                lifeform.addAttribute("Hunter/Browser", "These omnivores hunt prey, but settle for fruit, nuts, insects and roots much of the time.");
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.CURIOSITY);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.IMAGINATION);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.MOVEMENT);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.TASTE);
                break;
            case "Opportunist Browser":
                lifeform.addAttribute("Opportunist Browser", "Opportunist browsers forage for middle to high-energy plants, and may eat insects and small animals.");
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.CURIOSITY);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.IMAGINATION);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.CONCENTRATION);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.TASTE, 2);
                if (Dice.betweenOrEqual3d6(3, 8))
                    lifeform.addAttribute("Cast Iron Stomach", "Can eat or drink almost anything without getting sick.");
                break;
            case "Gatherer/Hunter":
                lifeform.addAttribute("Gatherer/Hunter", "Lives on grubs, fruit and seeds most of the time, but occasionally catch and eat a small animal.");
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.CURIOSITY);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.IMAGINATION);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.TASTE);
                break;
        }
    }

    private static void findCarnivoreSpeciality(Creature lifeform) {
        int bonus = 0;
        TreeMap<Integer, String> map = new TreeMap<>();
        if (lifeform.hasAttribute("Arctic")) bonus += 2;
        if (lifeform.hasAttribute("Swimmer")) bonus += 1;
        if (lifeform.hasAttribute("Aquatic")) bonus += 1;
        if (lifeform.hasAttribute("Amphibian")) bonus += 1;
        if (lifeform.hasAttribute("Plains Dweller")) bonus += 1;
        if (lifeform.hasAttribute("Forest Dweller")) bonus -= 1;

        String choice = TableMaker.makeRoll(
                Dice._2d6() + bonus,
                new int[]{0, 5, 8, 10, 12},
                new String[]{"Stalker", "Chaser", "Scavenger", "Pouncer", "Herder"}
        );
        switch (choice) {
            case "Stalker":
                lifeform.addAttribute("Stalker", "Stalking carnivores stealthily track down a single animal. They are very adaptable, and may use the tactics of a chaser or pouncer.");
                lifeform.addAttribute(AttributeEnum.IMAGINATION);
                lifeform.addAttribute(AttributeEnum.STEALTH);
                if (Dice.betweenOrEqual3d6(3, 12)) lifeform.addAttribute(AttributeEnum.CONCENTRATION);
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.EMPATHY);
                if (Dice.betweenOrEqual3d6(3, 10))
                    lifeform.addAttribute("Animal Empathy", "Has instincts about how other animals behave and act in given situations.");
                if (Dice.betweenOrEqual3d6(3, 6))
                    lifeform.addAttribute("Penetrating Call", "This is a measure of whether the species can sense, or comprehend, the feelings and attitudes of others.");
                if (Math.random() < 0.5)
                    lifeform.addAttribute("Tracker", "The species senses are routed for percive tracks and traces.");
                if (Dice.betweenOrEqual3d6(3, 12)) lifeform.addAttribute(AttributeEnum.MOVEMENT);
                if (Dice.betweenOrEqual3d6(3, 12)) lifeform.addAttribute(AttributeEnum.VOICE, 4);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.CONSTITUTION, 2);
                if (Dice.betweenOrEqual3d6(3, 10))
                    lifeform.addAttribute("Tracker", "The species senses are routed for percive tracks and traces.");
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.STEALTH);
                break;
            case "Chaser":
                lifeform.addAttribute("Chasers", "Prowl about for prey. When they find a target, they use a burst of speed to catch it. Chasers occasionally cooperate to bring down game. ");
                lifeform.addAttribute(AttributeEnum.MOVEMENT, 2);
                if (Dice.betweenOrEqual3d6(3, 12))
                    lifeform.addAttribute("Tracker", "The species senses are routed for percive tracks and traces.");
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.EMPATHY);
                if (Dice.betweenOrEqual3d6(3, 6))
                    lifeform.addAttribute("Animal Empathy", "Has instincts about how other animals behave and act in given situations.");
                if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.STAMINA, 4);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.STEALTH);
                break;
            case "Scavenger":
                lifeform.addAttribute("Carrion Scavenger", "Scavengers live off the leavings of other carnivore's meals and animals which have died of natural causes. Scavengers often hunt (usually by stalking) part-time to ensure a regular food supply. Sometimes, groups of scavengers drive a larger, solitary carnivore from its kill.");
                if (Math.random() < 0.35)
                    lifeform.addAttribute("Suspicion", "This trait determines how the species reacts toward new things-with pleasure, fear or distrust.");
                lifeform.addAttribute("Cast Iron Stomach", "Can eat or drink almost anything without getting sick.");
                if (Math.random() < 0.35)
                    lifeform.addAttribute("Empathy", -1, "This is a measure of whether the species can sense, or comprehend, the feelings and attitudes of others.");
                if (Math.random() < 0.35)
                    lifeform.addAttribute("Suspicion", "This trait determines how the species reacts toward new things-with pleasure, fear or distrust.");
                if (Math.random() < 0.5)
                    lifeform.addAttribute("Sense of Smell", "The scavenger lifeform uses the sense of smell to locate new sources of food.");
                if (Math.random() < 0.4)
                    lifeform.addAttribute("Stealth", "The lifeform uses a stealthy approach and is good at avoiding detection.");
                break;
            case "Pouncer":
                int trapperBonus = lifeform.hasAttribute("Burrower") ? 6 : 0;

                lifeform.addAttribute(AttributeEnum.REFLEXES, 2);
                if (Dice.betweenOrEqual3d6(3, 6 + trapperBonus)) {
                    lifeform.addAttribute("Trappers", "Build physical traps to snare prey.Trappers rarely stray far from their lairs, but are very strong and/or venomous, in order to quickly subdue struggling prey.");
                    lifeform.addAttribute("Alertness", "Alertness is the state of active attention by high sensory awareness such as being watchful and prompt to meet danger or emergency, or being quick to perceive and act.");
                    lifeform.addAttribute(AttributeEnum.CONCENTRATION);
                    if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.STRENGTH, 2);
                    if (Dice.betweenOrEqual3d6(3, 6))
                        lifeform.addAttribute("Venom", "This species uses toxinc to subdue a target.")
                                .addCondition("Fangs", "The venom is delivered by a bite (or eqivalent).");
                    if (Dice.betweenOrEqual3d6(3, 6))
                        lifeform.addAttribute("Webbing", "The lifeform produces a material that can be used for creating webs (for traps) or threads for movement or prey restriction.");
                } else {
                    lifeform.addAttribute("Pouncers", " Lie in wait in trees, on high rocks, or hide themselves in foliage.When a likely victim comes by, they leap fromhiding and tackle their prey. ");
                    lifeform.addAttribute(AttributeEnum.CONCENTRATION);
                    if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.STRENGTH);
                    if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.AGILITY);
                    if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.VISION);
                    if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.AUDIO);
                    if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.VOICE, 4);
                    if (Dice.betweenOrEqual3d6(3, 10)) lifeform.addAttribute(AttributeEnum.STEALTH);
                    if (Math.random() < 0.35)
                        lifeform.addAttribute("Tracker", "The species senses are routed for percive tracks and traces.");
                }
                break;
            case "Herder":
                lifeform.addAttribute("Herders", "These are similar to chasers, but they usually work in groups, against groups, using \"teamwork\" to single out a weak prey animal from its herd. ");
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.EMPATHY);
                lifeform.addAttribute(AttributeEnum.MOVEMENT);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.INTELLIGENCE);
                if (Dice.betweenOrEqual3d6(3, 8)) lifeform.addAttribute(AttributeEnum.VOICE, 4);
                if (Dice.betweenOrEqual3d6(3, 6)) lifeform.addAttribute(AttributeEnum.GREGARIOUSNESS);
                if (Math.random() < 0.3)
                    lifeform.addAttribute("Tracker", "The species senses are routed for percive tracks and traces.");
                if (Math.random() < 0.3)
                    lifeform.addAttribute("Tactics", "The species brain is wired to coordinate in teams and utilise position and terrain to maximum effect in its hunting");
                break;
        }
/*
 Not added
        Parasite

        Parasites survive by feeding off of a host creature. Typically, parasites attach themselves to a host lifeform (usually larger than the parasite) and feed off of it, usually to the hosts detriment. Some parasites (12 on 2d) are actually Symbiotes and serve as a vital part of the host creatures existence without harming the host.
        (Suspicion-1, Parasite, 6-9=Animal Empathy, 3-6=Venom, 3-10=Transference).
*/
    }

}
