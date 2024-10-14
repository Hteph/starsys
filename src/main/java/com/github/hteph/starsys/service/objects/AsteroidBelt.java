package com.github.hteph.starsys.service.objects;

import com.github.hteph.starsys.service.objects.wrappers.Presentations;
import com.github.hteph.utils.NumberUtilities;
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

		var divisor = BigDecimal.valueOf(2);
		var innerWidth = getOrbitalFacts().getOrbitalDistance().subtract(asteroidBeltWidth.divide(divisor, NumberUtilities.TWO));
		var outerWidth = getOrbitalFacts().getOrbitalDistance().add(asteroidBeltWidth.divide(divisor, NumberUtilities.TWO));
		facts.put("width",innerWidth.toPlainString()+" - "+outerWidth.toPlainString());
		facts.put("type", asteroidBeltType);
		facts.put("size distribution", parseSizeDistribution(sizeDistribution));

		return Presentations.builder()
							.facts(facts)
							.build();
	}
}
