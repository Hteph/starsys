package com.github.hteph.starsys.service.objects;


import com.github.hteph.starsys.enums.*;
import com.github.hteph.starsys.service.generators.NameGenerators;
import com.github.hteph.starsys.service.objects.wrappers.Homeworld;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.github.hteph.utils.StringUtils.nicefyName;

@Data
@Builder
@AllArgsConstructor
@Log4j2
public class Creature implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Homeworld homeworld;
    private Map<String, Attribute> attributes = new HashMap<>(); //TODO make Attribute wrapper and move logic to that instead.
    private TrophicLevels throphicLevel;
    private EnvironmentalEnum habitat;
    private ClimatePref climate;
    private CreatureBody body;

    public Creature(Homeworld place) {

        try {

            this.name = NameGenerators.femaleRomanNameGenerator.generate(8) + " " +
                        NameGenerators.femaleRomanNameGenerator.generate(8);

        } catch (Exception e) {
            this.name = place.getName().substring(0, 1 + place.getName().length() / 2) + "ians";
            log.warn("Error in naming of creature", e);
        }
        this.homeworld = place;
        this.description = "";
    }

    public Creature(Homeworld place, boolean proto) {

        this.name = "microorganisms";
        this.homeworld = place;
        this.description = proto ? "A multicellular lifeform" : "An ur-soup of microorganisms and basic building blocks of life";

    }

    public Map<String, Attribute> getAttributes() {
        //Can change details in the attributes but not remove or add new ones
        return new HashMap<>(attributes);
    }

    //TODO clean up the marsh of addAttribute, must be an clearer structure to this
    public Attribute addAttribute(String name, String description) {

        if (description.equals("null")) {
            log.warn("--------------Attribute: " + name + " has a null description ---------------------");
        }

        log.debug("Adding attribute = {}", name);

        if (hasAttribute(name)) {
            attributes.get(name.toLowerCase()).increaseLevel().addToDescription(description);
        } else {
            attributes.put(name.toLowerCase(), new Attribute(nicefyName(name), description, 1));
        }

        return attributes.get(name.toLowerCase());
    }

    public Attribute addAttribute(String name, int extras, String description) {
        if (description.equals("null")) {
            log.warn("--------------Attribute: " + name + " has a null description ---------------------");
        }
        Attribute attribute;
        if (hasAttribute(name)) {
            attribute = attributes.get(name.toLowerCase());

            if (extras < 0) {
                for (int i = 0; i > extras; i--) {
                    attribute.decreaseLevel().addToDescription(description); //TODO should really have a changeLevel method.
                }
            } else {
                for (int i = 0; i < extras; i++) {
                    attribute.increaseLevel().addToDescription(description);
                }
            }
        } else {
            attribute = new Attribute(nicefyName(name), description, extras);
            attributes.put(name.toLowerCase(), attribute);
        }

        return attributes.get(name);
    }

    public Attribute addAttribute(baseEnum enummet) {

        return addAttribute(enummet.getName().toLowerCase().trim(), enummet.getDescription()).setEnumCode(enummet);
    }

    public Attribute addAttribute(AttributeEnum enummet, int levels) {

        if (enummet.getDescription().equals("null")) {
            log.warn("--------------Enum: " + enummet + " has a null description ---------------------");
        }
        return addAttribute(enummet.getName().toLowerCase().trim(), levels, enummet.getDescription()).setEnumCode(enummet);
    }

    public boolean hasAttribute(String name) {
        if (attributes == null) {
            return false;
        }
        return attributes.containsKey(name.trim().toLowerCase());
    }

    public void removeAttribute(String name) {

        attributes.remove(name);
    }

    public void addToDescription(String description) {
        if (description.equals("null")) {
            log.warn("--------------A null description ---------------------");
        }
        this.description += "\n" + description;
    }

    public TrophicLevels getThrophicLevel() {
        return throphicLevel;
    }

    public Attribute setThrophicLevel(TrophicLevels trophicLevel) {
        this.throphicLevel = trophicLevel;

        return addAttribute(trophicLevel);
    }

    public boolean isThrophicLevel(TrophicLevels level) {
        return this.throphicLevel == level;
    }

    public boolean isClimatePref(ClimatePref climate) {
        return this.climate.equals(climate);
    }

    @Override
    public String toString() {
        StringBuilder attributeDesc = new StringBuilder();

        for (Map.Entry<String, Attribute> entry : getAttributes().entrySet()) {

            attributeDesc.append(entry.getValue().toString()).append("\n");
        }

        return "Sophont\n"
               + "name= " + name + "\n"
               + "description= " + description + "\n"
               + "homeworld= " + "Somewhere" + "\n"
               + "attributes= " + attributeDesc
               + "----------------";
    }
}
