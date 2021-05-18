package com.github.hteph.repository.objects;

import com.github.hteph.repository.objects.wrappers.Presentations;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashMap;

@Data
@SuperBuilder
public class AsteroidBelt extends StellarObject{

	private static final long serialVersionUID = 1L;
	private BigDecimal mass;
	private BigDecimal eccentricity;
	private String asteroidBeltType;
	private BigDecimal asteroidBeltWidth;
	private String objectClass;
	
	private double[] sizeDistribution; // Average size/ Max size

	@Override
	public String toString() {
		return getName() +"; asteroidBeltType: " + asteroidBeltType + ", Belt Width=" + asteroidBeltWidth
				+ ", sizeDistribution=" + parseSizeDistribution(sizeDistribution);
	}

	private String parseSizeDistribution(double[] sizeDistribution) {
		return "Average= "+sizeDistribution [0]+", Upper = "+sizeDistribution[1];
	}

	@Override
	public Presentations getPresentations() {

		HashMap<String, String> facts = new HashMap<>();

		facts.put("width",asteroidBeltWidth.toString());
		facts.put("type", asteroidBeltType);
		facts.put("size distribution", parseSizeDistribution(sizeDistribution));

		return Presentations.builder()
							.facts(facts)
							.build();
	}
}
