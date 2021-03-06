package com.github.hteph.repository.objects;

import com.github.hteph.repository.objects.wrappers.Presentations;
import com.github.hteph.utils.enums.StellarObjectType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashMap;

@Data
@SuperBuilder
public abstract class StellarObject implements Serializable, Comparable<StellarObject>{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String archiveID;
	private OrbitalFacts orbitalFacts;
	private StellarObjectType stellarObjectType;

	public abstract Presentations getPresentations();

	@Override
	public int compareTo(StellarObject o2) {

		return Double.compare(this.getOrbitalFacts().getOrbitalDistance().doubleValue(),
							  o2.getOrbitalFacts().getOrbitalDistance().doubleValue());
	}
}
