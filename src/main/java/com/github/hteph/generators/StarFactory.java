package com.github.hteph.generators;

import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.tables.StarClassificationTable;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NameGenerator;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StarFactory {

    static final MathContext TWO = new MathContext(2);
    static final MathContext THREE = new MathContext(3);
    static final MathContext FOUR = new MathContext(4);
    static final MathContext FIVE = new MathContext(5);

    public static Star get(String systemName, char systemPosition, Star star) {

        var randomNameGenerator = new NameGenerator();

        double mass = generateMass();
        //TODO This is for type V stars, should be expanded to handle other types, if the mass is too low to generate a decent temperture,
        // make a unique stellar object instead, a white dwarf or something
        int temperature = 100 * (int) (((500 + 4800 * Math.pow(mass, 0.5)) * (0.8 + Math.random() * 0.4)) / 100);
        if (temperature < 100 || temperature > 55000) {
            //generate something unique instead
        }
        double diameter = Math.pow(mass, 2 / 3.0);//Solar relative units
        double lumosity = Math.pow(mass, 3.5); //Solar relative units
        String starClass = StarClassificationTable.findStarClass(temperature) + " V";
        double maxAge = 10 * Math.pow(1 / mass, 2.5);
        double age = (0.3 + Math.random() * 0.6) * Math.min(maxAge, 13);// in billion of earth years

//TODO suspended this as it seems to be a bit overzealus in ingreasing the lumosity
//        double halfAgeBalance = 2 * age / maxAge;
//        lumosity *= Math.pow(halfAgeBalance, 0.5);
//        diameter *= Math.pow(halfAgeBalance, 1 / 3.0);

        //TODO abundance should be done nicer!
        int abundance = generateAbundance(age);

        String description =  starClass ;

        //TODO allow for multiple Starsystems, ie archiveID not hardcoded

        String starName;
        if(systemName.equals("random")) {
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

        return Star.builder()
                   .archiveID(systemName + " " + systemPosition)
                   .stellarObjectType(StellarObjectType.STAR)
                   .name(starName)
                   .age(BigDecimal.valueOf(age).round(TWO))
                   .description(description)
                   .luminosity(BigDecimal.valueOf(lumosity).round(THREE))
                   .mass(BigDecimal.valueOf(mass).round(THREE))
                   .diameter(BigDecimal.valueOf(diameter).round(THREE))
                   .classification(starClass)
                   .abundance(abundance)
                   .orbitalFacts(orbitalFacts.build())
                   .build();
    }

    private static int generateAbundance(double age) {
        int abundance;
        int[] abundanceArray = new int[]{0, 10, 13, 19, 22};
        int retVal = Arrays.binarySearch(abundanceArray, (int) (Dice._2d6() + age));
        if (retVal < 0) abundance = 2 - retVal + 1;
        else abundance = 2 - retVal;
        return abundance;
    }

    private static double generateMass() {
        //old code
//        double mass;
//        int testDice =Dice._3d6()-3;
//        double randN =testDice/(15.0+Math.random()/10); //turning the dice roll into a continous sligthly skewed randomnumber.
//        mass = 0.045/(0.001+Math.pow(randN,5)); // <-----------------------------------------MOST IMPORTANT STARTING POINT
        return 0.01 + (Dice.aLotOfd3(6) * Math.random() * Math.random() * Math.random());
    }

}

