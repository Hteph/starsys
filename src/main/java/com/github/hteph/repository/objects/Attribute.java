package com.github.hteph.repository.objects;



import com.github.hteph.utils.enums.AttributeEnum;
import com.github.hteph.utils.enums.baseEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Data
public class Attribute implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private int level;
	private HashMap<String, Attribute> conditions;
	private AttributeEnum enumCode;

    public Attribute(String name, String description, int level) {
		
		this.name=name;
		this.description = description;
		this.level=level;
		conditions = new HashMap<>();
	}
	
	public Attribute increaseLevel() {
		this.level++;
        return this;
    }
	
	public Attribute decreaseLevel() {

		this.level--;
        return this;
    }

	public HashMap<String, Attribute> getConditions() {
		return new HashMap<>(conditions);
	}

	public Attribute addCondition(String name, String description) { //I think this is wrong

		log.info("Adding condition = {}", name);
		int i=0;
		String suffix="";
		while(hasCondition(name)) {
        i++;
        suffix="+"+i;
		}

        conditions.putIfAbsent((name+suffix).toLowerCase(), new Attribute(name+suffix, description, 0));
        return this;
	}

	public Attribute addToDescription(String story){

	    if (!description.contains(story)) description +="\n"+story;

	    return this;
    }

	public boolean hasCondition(String name) {

		return !(conditions == null || conditions.isEmpty()) && conditions.containsKey(name.toLowerCase());
	}

    @Override
    public String toString() {
        StringBuilder attributeDesc=new StringBuilder();

        for (Map.Entry<String, Attribute> entry : getConditions().entrySet()) {

            attributeDesc.append(entry.getValue().toString()).append("\n");
        }

		return "Attributes"
							+ "name= " + name + "\n"
							+ "description= " + description + "\n"
							+ "level= " + level + "\n"
							+ "conditions= " + attributeDesc.toString() + "\n"
							+ "-------------";
    }

    public AttributeEnum getEnumCode() {
        return enumCode;
    }

    public Attribute setEnumCode(baseEnum enumCode) {

        if(enumCode instanceof AttributeEnum) this.enumCode = (AttributeEnum)enumCode;

        return this;
    }
}
