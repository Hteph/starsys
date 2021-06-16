package com.github.hteph.generators;


import com.github.hteph.repository.objects.CreatureBody;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.repository.objects.StellarObject;
import com.github.hteph.tables.BaseEnvironmentTable;
import com.github.hteph.tables.EnvironmentalAttributesTable;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.ClimatePref;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.Symmetry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatureGenerator {

    public static Creature generator(StellarObject place) {

        Creature lifeform= new Creature(place);
        BaseEnvironmentTable environment = new BaseEnvironmentTable(place);
        EnvironmentalEnum[] baseEnvironment = environment.findBaseEnvironment();

        if(!(baseEnvironment[1]==EnvironmentalEnum.NONE)) {
            lifeform.addAttribute("Dual Environment",baseEnvironment[0].getDescription()+"("+baseEnvironment[1]+")");
            lifeform.setHabitat(baseEnvironment[1]);
        }else lifeform.setHabitat(baseEnvironment[0]);

        EnvironmentalAttributesTable.environmentalAttributes(lifeform, baseEnvironment);
        decideMetabolism(lifeform);
        gravityEffects(lifeform);
        basicBodyShape(lifeform);

        return lifeform;

    }

    private static void basicBodyShape(Creature lifeform) {
        int bonus=0;
        int bonus2=0;
        lifeform.setBody(new CreatureBody());

        if(lifeform.hasAttribute("climber")
                || lifeform.hasAttribute("brachiator")) bonus+=3;
        if (lifeform.hasAttribute("flier")
                && lifeform.getAttributes().get("flier").hasCondition("winged")) bonus+=3;

        if (lifeform.hasAttribute("Aquatic")) bonus -=3;
        if(lifeform.getHabitat().equals(EnvironmentalEnum.EXOTIC)) bonus2 -=6;


        if(Dice._3d6(16+bonus+bonus2)) {
            lifeform.getBody().setBodySymmetry(Symmetry.BILATERAL);
            lifeform.getBody().setLimbSegments(TableMaker.makeRoll(
                    Dice.d6(),
                    new int[]{1,2,6},
                    new Integer[]{1,2,Dice.d6()+2}));

        } else if(Dice._3d6(16+bonus2)){
            int sides = Dice.d6()+2;
            switch(sides){
                case 3: lifeform.getBody().setBodySymmetry(Symmetry.TRILATERAL);
                    break;
                case 4: lifeform.getBody().setBodySymmetry(Symmetry.QUADRAL);
                    break;
                case 5: lifeform.getBody().setBodySymmetry(Symmetry.PENTRADAL);
                    break;
                default:
                    lifeform.getBody().setBodySymmetry(Symmetry.RADIAL);
                    break;
            }
            lifeform.getBody().setLimbSegments(TableMaker.makeRoll(
                    Dice._2d6(),
                    new int[]{2,6,10},
                    new Integer[]{1,2,Dice.d6()+2}));
        } else {
            lifeform.getBody().setBodySymmetry(Symmetry.NONE);
            lifeform.getBody().setLimbSegments(0);
        }
    }

    private static void gravityEffects(Creature lifeform){

    double gravity =((Planet)(lifeform.getHomeworld())).getGravity().doubleValue();
    int roll=Dice._3d6();

    if(gravity<0.7) {
        int gravityeffect = (int) (10*(gravity-1));
        roll +=gravityeffect;
        lifeform.addAttribute(AttributeEnum.STRENGTH, gravityeffect/2);
        lifeform.addAttribute(AttributeEnum.CONSTITUTION, -1);
        lifeform.addAttribute("G-Tolerance", -1,"The lifeform is sensitive to acceleration forces");
        lifeform.addAttribute(AttributeEnum.FRAME, gravityeffect/2);

    }else if (gravity>1.5) {
        int gravityEffects = (int) NumberUtilities.squared(gravity);
        roll +=gravityEffects;
        lifeform.addAttribute("G-Tolerance", 1,"The Lifeform is very tolerant towards acceleration forces");
        lifeform.addAttribute(AttributeEnum.STRENGTH, (int) NumberUtilities.sqrt((double) gravityEffects));
        lifeform.addAttribute(AttributeEnum.CONSTITUTION, gravityEffects/2);
        lifeform.addAttribute(AttributeEnum.FRAME, gravityEffects);
    }

        String choice = TableMaker.makeRoll(
                roll,
                new int[]{0, 7, 16, 17},
                new String[]{"Acceleration Weakness","None", "Space Sickness","Acceleration Tolerance"}
        );

        switch (choice){
            case "Acceleration Weakness":
                lifeform.addAttribute("Acceleration Weakness", "The lifeform is sensitive to G-forces and may blackout or hemmorage at a much lower threshold than the average lifeform");
                break;
            case "Space Sickness":
                lifeform.addAttribute("Space Sickness","The balance organs of the lifeform has a difficult time to handle free-fall and micro gravity, probably suffering from debilitating nausea");
                break;
            case "Acceleration Tolerance":
                lifeform.addAttribute("Acceleration Tolerance","The lifeform is resistance to G-forces and may blackout or hemmorage at a much higher threshold than the average lifeform");
                break;
            default:
                break;
        }
}

    private static void decideMetabolism(Creature lifeform) {

        int bonus=0;
        if(lifeform.isClimatePref(ClimatePref.COLD)) bonus +=1;
        if(lifeform.hasAttribute("Aquatic")) bonus -=1;
        if(lifeform.hasAttribute("Desert Dweller")) bonus -=1;
        if(lifeform.hasAttribute("Chaser")) bonus +=1;
        if(lifeform.hasAttribute("Pouncer")) bonus +=1;
        if(lifeform.hasAttribute("Herder")) bonus +=1;
        if(lifeform.hasAttribute("Ergivore")) bonus -=2;
        if(lifeform.hasAttribute("Flier")) bonus +=1;

        String choice=TableMaker.makeRoll(
                        Dice.d6()+bonus,
                        new int[]{0, 2, 4, 6},
                        new String[]{"Slow Metabolism", "Cold Blooded", "Warm Blooded", "Hyperactive"}
                );
        switch (choice){
            case "Slow Metabolism":
                lifeform.addAttribute("Slow Metabolism","The metabolic processes are so slow that the lifeform seems to live in another time paradigm. ");
                lifeform.addAttribute(AttributeEnum.SPEED, -2).addToDescription("The slow metabolism of this creature lowers its reaction times");
                lifeform.addAttribute(AttributeEnum.LIFESPAN, 2);
                lifeform.addAttribute(AttributeEnum.SUSTENANCE, -2);
                break;
            case "Cold Blooded":
                lifeform.addAttribute("Cold Blooded","The lifeform is dependent on the ambient temperatue to be above a certain temperature threshold.");
                lifeform.addAttribute("Ectothermy", "Controlling body temperature through external metabolic processes, such as by basking in the sun");
                lifeform.addAttribute(AttributeEnum.LIFESPAN, 1);
                lifeform.addAttribute(AttributeEnum.SUSTENANCE, -1);
                break;
            case "Warm Blooded":
                lifeform.addAttribute("Warm Blooded","The metabolism of this lifeform can support it in a wide range of temperatures");
                break;
            case "Hyperactive":
                lifeform.addAttribute("Hyperactive","Is equally home in water as on land");
                lifeform.addAttribute("Homeothermy","The lifeform maintains a stable internal body temperature regardless of external influence.");
                lifeform.addAttribute(AttributeEnum.SPEED, 1);
                lifeform.addAttribute(AttributeEnum.LIFESPAN, -1);
                lifeform.addAttribute(AttributeEnum.SUSTENANCE, 2);
                break;
        }
    }
}
