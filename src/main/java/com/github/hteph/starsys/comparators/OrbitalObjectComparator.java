package com.github.hteph.starsys.comparators;


import com.github.hteph.starsys.service.objects.StellarObject;

import java.io.Serializable;
import java.util.Comparator;

public class OrbitalObjectComparator implements Comparator<StellarObject>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(StellarObject o1, StellarObject o2) {

		return Double.compare(o2.getOrbitalFacts().getOrbitalDistance().doubleValue(),
							  o1.getOrbitalFacts().getOrbitalDistance().doubleValue());
	}

}
