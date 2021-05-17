package com.github.hteph.utils.comparators;


import com.github.hteph.repository.objects.StellarObject;
import com.github.hteph.repository.objects.TempOrbitalObject;

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
