package com.github.hteph.generators;

import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.StarClassificationTable;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NameGenerator;
import com.github.hteph.utils.NumberUtilities;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StarFactory {

    static final MathContext TWO = new MathContext(2);
    static final MathContext THREE = new MathContext(3);
    static final MathContext FOUR = new MathContext(4);
    static final MathContext FIVE = new MathContext(5);

    public static Star get(String systemName, char systemPosition, Star star) {

        List<String> descriptors = new ArrayList<>();
        var randomNameGenerator = new NameGenerator();

        double mass;
        int temperature;
        //TODO This is for type V stars, should be expanded to handle other types, if the mass is too low to generate a decent temperture,
        // make a unique stellar object instead, a white dwarf or something, if to high make a giant star
        do {
            mass = generateMass();
            temperature = 100 * (int) (((500 + 4800 * Math.pow(mass, 0.5)) * (0.8 + Math.random() * 0.4)) / 100);
        }while (temperature<100 || temperature>28000);

        double diameter = Math.pow(mass, 2 / 3.0);//Solar relative units
        double lumosity = Math.pow(mass, 3.5); //Solar relative units
        String starClass = StarClassificationTable.findStarClass(temperature) + " V";
        double maxAge = 10 * Math.pow(1 / mass, 2.5);
        double age = (0.35 + Dice.d3()/10d+Math.random()/10d) * Math.min(maxAge, 13);// in billion of earth years

        if(age/maxAge>0.9) descriptors.add("Near end of lifetime");
//TODO suspended this as it seems to be a bit overzealus in ingreasing the lumosity
//        double halfAgeBalance = 2 * age / maxAge;
//        lumosity *= Math.pow(halfAgeBalance, 0.5);
//        diameter *= Math.pow(halfAgeBalance, 1 / 3.0);

        //TODO abundance should be done nicer!
        int abundance = generateAbundance((int) age);

        if(abundance > 4 ) descriptors.add("Resource Rich System");

        //TODO allow for multiple Starsystems, ie archiveID not hardcoded

        String starName;
        if(systemName.equals("random") || systemName.equals("life")) {
            try {
                starName = randomNameGenerator.compose((3 + Dice.d6()));
            } catch (Exception e) {
                starName = "Unknown";
                log.warn("++++++++++Name failed+++++++++++++");
            }
        } else{
            starName = systemName;
        }
        //For now there can only be one star in each system
        var orbitalFacts = OrbitalFacts.builder()
                                       .orbitalDistance(BigDecimal.ZERO)
                                       .orbitalPeriod(BigDecimal.ZERO);

        String description = starClass + ": " + String.join(", ", descriptors);

        //TODO fix systemName ArchiveID isn't used but multiple starSystems probably will

        return Star.builder()
                   .archiveID(systemName + " " + systemPosition)
                   .stellarObjectType(StellarObjectType.STAR)
                   .name(starName)
                   .age(BigDecimal.valueOf(age).round(TWO))
                   .description(starClass)
                   .luminosity(BigDecimal.valueOf(lumosity).round(THREE))
                   .mass(BigDecimal.valueOf(mass).round(THREE))
                   .diameter(BigDecimal.valueOf(diameter).round(THREE))
                   .classification(description)
                   .abundance(abundance)
                   .orbitalFacts(orbitalFacts.build())
                   .build();
    }

    private static int generateAbundance(int age) {

        int[] abundanceArray = new int[]{0, 10, 13, 19, 22};
        List<Integer> resultList = List.of(0, 1, 2, 3, 4);

        return TableMaker.makeRoll(Dice._2d6() + age, abundanceArray,resultList);
    }

    private static double generateMass() {
        //old code
//        double mass;
//        int testDice =Dice._3d6()-3;
//        double randN =testDice/(15.0+Math.random()/10); //turning the dice roll into a continous sligthly skewed randomnumber.
//        mass = 0.045/(0.001+Math.pow(randN,5)); // <-----------------------------------------MOST IMPORTANT STARTING POINT
//        return 0.01 + ((Dice.aLotOfd3(6)-2) * Math.random() * Math.random() * Math.random()* Math.random());

        double a = -5.0407;
        double b = -69.232;
        double t = -0.220214;

        double baseMass =Math.random() + Dice._3d6()-3;

        return ( a /(1+ b * Math.exp( t *(baseMass))));
    }

}

