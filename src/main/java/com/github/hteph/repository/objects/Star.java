package com.github.hteph.repository.objects;

import com.github.hteph.repository.objects.wrappers.Presentations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Star extends StellarObject {

    private static final long serialVersionUID = 1L;

    private BigDecimal luminosity;
    private BigDecimal diameter;
    private String classification;
    private BigDecimal age;
    private ArrayList<StellarObject> orbitalObjects;
    private int abundance;
    private BigDecimal mass;

    public String toString() {
        return "Star: " + getName() + " (" + classification + ")";
    }

    @Override
    public Presentations getPresentations() {
        HashMap<String, String> facts = new HashMap<>();

        facts.put("luminosity", luminosity.toString());
        facts.put("age", age.toString());
        facts.put("mass",mass.toString());
        facts.put("abundance",String.valueOf(abundance));

        return Presentations.builder()
                            .facts(facts)
                            .build();
    }
}