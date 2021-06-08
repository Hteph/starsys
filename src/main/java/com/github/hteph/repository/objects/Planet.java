package com.github.hteph.repository.objects;


import com.github.hteph.repository.objects.wrappers.Presentations;
import com.github.hteph.utils.enums.Breathing;
import com.github.hteph.utils.enums.HydrosphereDescription;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Planet extends StellarObject {

    private static final long serialVersionUID = 1L;

    private BigDecimal mass;
    private int radius;
    private BigDecimal gravity; //g
    private BigDecimal density; //Earth normal
    private BigDecimal axialTilt;

    private boolean tideLockedStar;
    private boolean resonanceOrbitalPeriod;
    private BigDecimal rotationalPeriod;
    private String tectonicCore;
    private BigDecimal magneticField;
    private String tectonicActivityGroup;
    private BigDecimal albedo;

    private HydrosphereDescription hydrosphereDescription;
    private int hydrosphere;
    private Set<AtmosphericGases> atmosphericComposition;
    private BigDecimal atmoPressure;
    private boolean boilingAtmo;
    private TemperatureFacts temperatureFacts;

    private List<Planet> moonList;

    private BigDecimal lunarTidal;
    private boolean planetLocked;
    private BigDecimal lunarOrbitalPeriod;
    private BigDecimal lunarOrbitDistance; //in planetRadii

    private String classificationName;

    private Breathing lifeType;
    private Biosphere life;

    public String getAtmosphericCompositionParsed() {

        if(atmosphericComposition == null || atmosphericComposition.isEmpty()) return "";

        ArrayList<AtmosphericGases> atmo = new ArrayList<>(atmosphericComposition);

        atmo.sort(Comparator.reverseOrder());

        return "["+ atmo.stream().map(AtmosphericGases::toString).collect(Collectors.joining(", "))+"]";
    }

    @Override
    public String toString() {

        var moons = moonList != null ? ", Number of moons= " + moonList.size() : "";
        var tectonics = tectonicActivityGroup != null ? ", Tectonics: " + tectonicActivityGroup : "";
        var hydro = hydrosphereDescription != null ? ", Hydrospheric Description: " + hydrosphereDescription.label : "";
        String hydroPercentage = hydrosphereDescription != null && hydrosphereDescription != HydrosphereDescription.NONE
                ? " ( " + hydrosphere + " %)"
                : "";
        var life = lifeType != null && lifeType != Breathing.NONE ? ", Life Type: " + lifeType.label : "";
        var atmo = atmoPressure != null
                ? ", Atmospheric Pressure = " + atmoPressure + " (" + getAtmosphericCompositionParsed() + ")"
                : "";

        var dayNightTemp = temperatureFacts.getDayTempMod() != null && temperatureFacts.getNightTempMod() != null
                ? " D/N mod: " + temperatureFacts.getDayTempMod() + "/" + temperatureFacts.getNightTempMod()
                : "";

        var almenack = ", Length of day = " + rotationalPeriod + " E-h, length of year = " + super.getOrbitalFacts()
                                                                                                  .getOrbitalPeriod() + " E-y";

        return super.getName() + ": " + super.getDescription() + ", radius=" + radius + " km, [" + getGravity() + "g]" + tectonics
                + hydro + ", " + hydroPercentage + atmo + ", Surface temperature= "
                + (temperatureFacts.getSurfaceTemp() - 373) + " C" + dayNightTemp + almenack + life
                + moons;
    }

    public Presentations getPresentations() {

        HashMap<String, String> facts = new HashMap<>();
        var presentationBuilder = Presentations.builder();

        facts.put("mass", mass.toPlainString());
        facts.put("rotation period", rotationalPeriod.toPlainString());
        facts.put("radius", String.valueOf(radius));
        facts.put("axial tilt", axialTilt.toPlainString()+"\u00B0");
        facts.put("orbital eccentricity", getOrbitalFacts().getOrbitalEccentricity().toPlainString());
        facts.put("magnetic field", magneticField.toPlainString());
        facts.put("orbital period", getOrbitalFacts().getOrbitalPeriod().toPlainString());

        facts.put("resonance", String.valueOf(resonanceOrbitalPeriod));
        facts.put("inclination", getOrbitalFacts().getOrbitalInclination().toPlainString()+"\u00B0");

        if (getStellarObjectType() != StellarObjectType.JOVIAN) {
            facts.put("gravity", gravity.toPlainString());
            facts.put("density", density.toPlainString());
            facts.put("tidelocked", String.valueOf(tideLockedStar));

            facts.put("tectonic core", tectonicCore);
            facts.put("tectonic activity group", tectonicActivityGroup);
            facts.put("Hydrosphere description", hydrosphereDescription.label);
            facts.put("hydrosphere", String.valueOf(hydrosphere));
            facts.put("atmospheric composition", getAtmosphericCompositionParsed());
            facts.put("atmospheric pressure", atmoPressure.toPlainString());
            facts.put("Surface temperature", String.valueOf(temperatureFacts.getSurfaceTemp()-273)+"\u2103");

            facts.put("average rangeband temperature", getStringFromInts(temperatureFacts.getRangeBandTemperature()));
            facts.put("summer rangeband temperature", getStringFromInts(temperatureFacts.getRangeBandTempSummer()));
            facts.put("winter rangeband temperature", getStringFromInts(temperatureFacts.getRangeBandTempWinter()));

            facts.put("pressure",atmoPressure.toPlainString());
            facts.put("composition",getAtmosphericCompositionParsed());
            if(lifeType != null && lifeType != Breathing.NONE) facts.put("life type",lifeType.label);
        }

        if (getStellarObjectType() != StellarObjectType.MOON) {
            if(moonList != null && !moonList.isEmpty()){
                facts.put("number of moons", String.valueOf(moonList.size()));
            }
            facts.put("tide locked to star", String.valueOf(tideLockedStar));
            if (moonList != null && !moonList.isEmpty()) {
                presentationBuilder.MoonPresentations(moonList.stream()
                                                              .map(Planet::getPresentations)
                                                              .collect(Collectors.toList()));
            }
        } else {
            facts.put("name",getName());
            facts.put("description",getDescription());

            int calc = (int) lunarOrbitDistance.doubleValue()
                    * ((Planet)(getOrbitalFacts().getOrbitsAround())).radius *2;
            facts.put("orbitDistance",String.valueOf(calc));
            if(planetLocked) facts.put("planetLocked", "Yes");
            facts.put("lunarOrbitalPeriod", lunarOrbitalPeriod.toPlainString());
            facts.put("rotation", rotationalPeriod.toPlainString());

        }

        return presentationBuilder.facts(facts)
                                  .build();
    }

    private String getStringFromInts(int[] rangeBandTemperature) {
//TODO fix this into a html table instead?
        return IntStream.of(rangeBandTemperature)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining("; ", "[", "]"));
    }
}

