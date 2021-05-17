package com.github.hteph.utils.comparators;


import com.github.hteph.repository.objects.TempOrbitalObject;

import java.io.Serializable;
import java.util.Comparator;

public class tempOrbitalObjectComparator implements Comparator<TempOrbitalObject>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(TempOrbitalObject o1, TempOrbitalObject o2) {

		return Double.compare(o1.getOrbitDistance(), o2.getOrbitDistance());
	}

}
