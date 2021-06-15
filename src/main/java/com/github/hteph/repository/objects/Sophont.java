package com.github.hteph.repository.objects;



import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NameGenerator;
import com.github.hteph.utils.enums.Attributes;
import com.github.hteph.utils.enums.ClimatePref;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.TrophicLevels;
import com.github.hteph.utils.enums.baseEnum;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Sophont implements Serializable {

    private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private StellarObject homeworld;
	private Map<String,Attribute> attributes = new HashMap<>();
    private TrophicLevels throphicLevel;
    private EnvironmentalEnum habitat;
    private ClimatePref climate;
    private CreatureBody body;

// Constructor -------------------------------------------
	
	public Sophont(StellarObject place) {
		
		NameGenerator randomName;
		try {
			randomName = new NameGenerator();

			int randomNummer = 2+ Dice.aLotOfd3(3);
			this.name=randomName.compose(randomNummer)+" of "+place.getName();

		} catch (Exception e) {
			this.name = place.getName().substring(0,1+place.getName().length()/2)+"ians";
			e.printStackTrace();
		}
		this.homeworld=place;
	}

	public Map<String, Attribute> getAttributes() {
		//Can change details in the attributes but not remove or add new ones
		return new HashMap<>(attributes);
	}

	public Attribute addAttribute(String name, String description) {
        Attribute attribute;
		if(hasAttribute(name))attribute =  attributes.get(name).increaseLevel().addToDescription(description);
		else attribute=attributes.put(name, new Attribute(name, description));
        return attribute;
	}

	public Attribute addAttribute(String name, int extras, String description) {
        Attribute attribute;
		if(hasAttribute(name)){
		    if(extras<0)attribute = attributes.get(name).decreaseLevel().addToDescription(description);
		    else attribute = attributes.get(name).increaseLevel().addToDescription(description);
        }
		else if (extras<0){
		    attribute = new Attribute(name, description);
			attributes.put(name, attribute);
			for(int i=0;i>extras-1;i--) attribute.decreaseLevel();//Compensating for the first "free" level.
		}else{
            attribute = new Attribute(name, description);
            attributes.put(name, attribute);
            for(int i=0;i<extras;i++) attribute.increaseLevel();
        }
        return attribute;
	}

	public Attribute addAttribute(baseEnum enummet){

	   return addAttribute(enummet.getName(), enummet.getDescription()).setEnumCode(enummet);
    }

    public Attribute addAttribute(Attributes enummet, int levels){

        return addAttribute(enummet.getName(),levels, enummet.getDescription()).setEnumCode(enummet);
    }
	
	public boolean hasAttribute(String name) { return attributes.containsKey(name.trim().toLowerCase()); }
	
	public void removeAttribute(String name) {

		attributes.remove(name);
	}

    public void addToDescription(String description) {

	    this.description +="\n"+description;
    }

    public TrophicLevels getThrophicLevel() {
        return throphicLevel;
    }

    public Attribute setThrophicLevel(TrophicLevels throphicLevel) {
        this.throphicLevel = throphicLevel;

        return addAttribute(throphicLevel);
    }

    public boolean isThrophicLevel(TrophicLevels level){
	    return this.throphicLevel==level;
    }

    public EnvironmentalEnum getHabitat() {
        return habitat;
    }

    public void setHabitat(EnvironmentalEnum habitat) {
        this.habitat = habitat;
    }

    public ClimatePref getClimate() {
        return climate;
    }

    public void setClimate(ClimatePref climate) {
        this.climate = climate;
    }

    public boolean isClimatePref(ClimatePref climate){
	    return this.climate.equals(climate);

    }

    public CreatureBody getBody() {
        return body;
    }

    public void setBody(CreatureBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
	    StringBuilder attributeDesc=new StringBuilder();

        for (Map.Entry<String, Attribute> entry : getAttributes().entrySet()) {

            attributeDesc.append(entry.getValue().toString()).append("\n");
        }

        return "Sophont\n" +
                "name= " + name + "\n" +
                "description= " + description + "\n" +
                "homeworld= " + "Somewhere" + "\n" +
                "attributes= " + attributeDesc.toString() +
                "----------------";
    }
}
