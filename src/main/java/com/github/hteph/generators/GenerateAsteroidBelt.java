package com.github.hteph.generators;

import com.github.hteph.repository.objects.AsteroidBelt;
import com.github.hteph.repository.objects.OrbitalFacts;
import com.github.hteph.repository.objects.Star;
import com.github.hteph.repository.objects.TempOrbitalObject;
import com.github.hteph.tables.TableMaker;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.TreeSet;

import static com.github.hteph.utils.NumberUtilities.sqrt;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenerateAsteroidBelt {


    static final MathContext TWO = new MathContext(2);
    static final MathContext THREE = new MathContext(3);
    static final MathContext FOUR = new MathContext(4);
    static final MathContext FIVE = new MathContext(5);


    public static AsteroidBelt generator(String archiveID,
                                         String name,
                                         String description,
                                         TempOrbitalObject tempObject,
                                         Star orbitingAround,
                                         TreeSet<TempOrbitalObject> tempOrbitalObjects) {

        boolean outerZone = false;

        //AsteroidBelt belt = new AsteroidBelt(archiveID, name, description, BigDecimal.valueOf(tempObject.getOrbitDistance()), orbitingAround);

        var orbitalFacts = OrbitalFacts.builder()
                                       .orbitalDistance(BigDecimal.valueOf(tempObject.getOrbitDistance()).round(THREE));
        var asteroidBeltBuilder = AsteroidBelt.builder()
                                              .archiveID(archiveID)
                                              .name(name)
                                              .stellarObjectType(StellarObjectType.ASTEROID_BELT)
                                              .description(description);

        double snowLine = 5 * Math.pow(orbitingAround.getLuminosity().doubleValue(), 0.5);
        if (tempObject.getOrbitDistance() > snowLine) outerZone = true;

        orbitalFacts.orbitalEccentricity(BigDecimal.valueOf(((Dice.d6() - 1) * (Dice.d6() - 1))
                                                                    / (100.0 * Dice.d6())).round(TWO));

        asteroidBeltBuilder.asteroidBeltType(getBeltType(tempObject, orbitingAround, outerZone));

        //  belt.setMass((Dice.d10()) * Math.pow(10, 4 - getMassBase(orbitingAround, outerZone))); //TODO check this it look wrong to me, also not appropiate for Planetary rings
//TODO implement planetary ring
//        if (tempObject.getOrbitObject() == 'j' || tempObject.getOrbitObject() == 'J') {
//            asteroidBeltBuilder.objectClass("Planetary Ring");
//
//            asteroidBeltBuilder.sizeDistribution(new double[]{0.001, 0.01});
//            asteroidBeltBuilder.asteroidBeltWidth(BigDecimal.ZERO);
//        } else {
            asteroidBeltBuilder.objectClass("Asteroid belt");
            asteroidBeltBuilder.asteroidBeltWidth(BigDecimal.valueOf(getBeltWitdth(tempObject, tempOrbitalObjects)).round(TWO));
            asteroidBeltBuilder.sizeDistribution(new double[]{getMainAverageSize(), getLargestSizeAsteroids()});
 //       }

        asteroidBeltBuilder.orbitalFacts(orbitalFacts.build());
        return asteroidBeltBuilder.build();
   }

    private static double getMainAverageSize() {
        Double[] mainAverage = {0.001, 0.005, 0.01, 0.025, 0.05, 0.1, 0.3, 0.5};
        int[] diceNumbers = {2, 3, 5, 7, 8, 10, 11, 12};
        return TableMaker.makeRoll(Dice._2d6(), diceNumbers, mainAverage);
    }

    private static Double getLargestSizeAsteroids() {
        Double[] maxSize = {1.0, 5.0, 10.0, 50.0, 100.0, 500.0};
        int[] dieNumbers = {1, 2, 3, 4, 5, 6};
        return TableMaker.makeRoll(Dice.d6(), dieNumbers, maxSize);
    }

    private static double getBeltWitdth(TempOrbitalObject tempObject, TreeSet<TempOrbitalObject> tempOrbitalObjects) {
        double beltWitdth;

        var inwardOrbitalObject = tempOrbitalObjects.lower(tempObject);

        if (inwardOrbitalObject != null && !tempOrbitalObjects.headSet(tempObject).isEmpty()) {

            beltWitdth = tempObject.getOrbitDistance() - inwardOrbitalObject.getOrbitDistance();
            if (inwardOrbitalObject.getOrbitObject() == 'j' || inwardOrbitalObject.getOrbitObject() == 'J')
                beltWitdth /= 2.0;

        } else beltWitdth = tempObject.getOrbitDistance() / 2.0;


        var outwardOrbitalObject = tempOrbitalObjects.higher(tempObject);

        if (outwardOrbitalObject != null
                && !tempOrbitalObjects.tailSet(tempObject).isEmpty()
                && (outwardOrbitalObject.getOrbitObject() == 'j' || outwardOrbitalObject.getOrbitObject() == 'J')) {
            beltWitdth /= 2.0;

        }

        return beltWitdth;
    }

    private static String getBeltType(TempOrbitalObject tempObject, Star orbitingAround, boolean outerZone) {
        double density;
        if (!outerZone) {
            double lum = orbitingAround.getLuminosity().doubleValue();
            density = 0.3 + (Dice._2d6() - 2)
                    * 0.127
                    / Math.pow(0.4 + (tempObject.getOrbitDistance() / sqrt(lum)), 0.67);
        } else {
            density = 0.3 + (Dice._2d6() - 2) * 0.05;
        }


        String asterioidBeltType;// TODO the general type and composition of the belt can be further fleshed out
        //TODO make this to table maker
        int[] typeArray = new int[]{-2, -1, 6, 11};
        String[] asterioidBeltTypeArray = new String[]{"Metallic", "Silicate", "Carbonaceous", "Icy", "Icy"};
        double[] densityArray = new double[]{0, 0.8, 1, 1.2};

        int densMod = Arrays.binarySearch(densityArray, density);
        if (outerZone) densMod += 6;
        if (tempObject.getOrbitDistance() < 0.75 * Math.sqrt(orbitingAround.getLuminosity().doubleValue()))
            densMod -= 2;

        int retVal = Arrays.binarySearch(typeArray, Dice._2d6() + densMod);

        if (retVal < 0) asterioidBeltType = asterioidBeltTypeArray[-retVal - 1];
        else asterioidBeltType = asterioidBeltTypeArray[retVal];
        return asterioidBeltType;
    }

    private static int getMassBase(Star orbitingAround, boolean outerZone) {
        int[] massArray = new int[]{0, 5, 7, 9, 11};
        Integer[] massBaseArray = new Integer[]{0, 5, 7, 9, 11};
        int massMod = orbitingAround.getAbundance();
        if (outerZone) massMod += 2;
        if (orbitingAround.getAge().doubleValue() > 7) massMod -= 1;
        //TODO +2 from multiple star system

        return TableMaker.makeRoll(Dice._2d6() + massMod, massArray, massBaseArray);
    }


}
