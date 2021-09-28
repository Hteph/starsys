package com.github.hteph.repository.objects;



import com.github.hteph.repository.objects.wrappers.Homeworld;
import com.github.hteph.utils.Dice;
import com.github.hteph.utils.NameGenerator;
import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.ClimatePref;
import com.github.hteph.utils.enums.EnvironmentalEnum;
import com.github.hteph.utils.enums.TrophicLevels;
import com.github.hteph.utils.enums.baseEnum;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Log4j2
public class Creature implements Serializable {

    private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private Homeworld homeworld;
	private Map<String,Attribute> attributes = new HashMap<>();
    private TrophicLevels throphicLevel;
    private EnvironmentalEnum habitat;
    private ClimatePref climate;
    private CreatureBody body;

// Constructor -------------------------------------------
	
	public Creature(Homeworld place) {
		
		NameGenerator randomName;
		try {
			randomName = new NameGenerator();


			this.name=randomName.compose(2+ Dice.aLotOfd3(3))+" "+
                    randomName.compose(2+ Dice.aLotOfd3(3));

		} catch (Exception e) {
			this.name = place.getName().substring(0,1+place.getName().length()/2)+"ians";
			log.warn("Error in naming of creature ={}", e.getMessage());
		}
		this.homeworld=place;
		this.description = "";
	}

	public Creature(Homeworld place, boolean proto) {

		this.name = "microorganisms";
		this.homeworld = place;
		this.description = "An ur-soup of microorganisms and basic building blocks of life";

	}

	public Map<String, Attribute> getAttributes() {
		//Can change details in the attributes but not remove or add new ones
		return new HashMap<>(attributes);
	}
//TODO clean up the marsh of addAttribute, must be an clearer structure to this
	public Attribute addAttribute(String name, String description) {

	    if(description.equals("null")) log.warn("--------------Attribute: "+name+" has a null description ---------------------");

		if(hasAttribute(name)) attributes.get(name).increaseLevel().addToDescription(description);
		else attributes.put(name, new Attribute(name.trim().toLowerCase(), description));

        return attributes.get(name);
	}

	public Attribute addAttribute(String name, int extras, String description) {
        if(description.equals("null")) log.warn("--------------Attribute: "+name+" has a null description ---------------------");
        Attribute attribute;
		if(hasAttribute(name)){
		    if(extras<0)attributes.get(name).decreaseLevel().addToDescription(description);
		    else attributes.get(name).increaseLevel().addToDescription(description);
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
        return attributes.get(name);
	}

	public Attribute addAttribute(baseEnum enummet){

	   return addAttribute(enummet.getName(), enummet.getDescription()).setEnumCode(enummet);
    }

    public Attribute addAttribute(AttributeEnum enummet, int levels){

        if(enummet.getDescription().equals("null")) log.warn("--------------Enum: "+enummet+" has a null description ---------------------");
        return addAttribute(enummet.getName(),levels, enummet.getDescription()).setEnumCode(enummet);
    }
	
	public boolean hasAttribute(String name) { return attributes.containsKey(name.trim().toLowerCase()); }
	
	public void removeAttribute(String name) {

		attributes.remove(name);
	}

    public void addToDescription(String description) {
        if(description.equals("null")){
            log.warn("--------------A null description ---------------------");
        }
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


    public boolean isClimatePref(ClimatePref climate) {
		return this.climate.equals(climate);
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
