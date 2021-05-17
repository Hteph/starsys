package com.github.hteph.repository.objects;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;

@Data
@Builder
public class AtmosphericGases implements Serializable, Comparable<AtmosphericGases>{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private int percentageInAtmo;

	@Override
	public String toString() {
        return name + " (" + percentageInAtmo + " %)";
	}

    @Override
    public int compareTo(AtmosphericGases otherGas) {
        return this.getPercentageInAtmo()-otherGas.getPercentageInAtmo();
    }

    public static class atmoCompositionComparator implements Comparator<AtmosphericGases>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(AtmosphericGases gas1, AtmosphericGases gas2) {

            // Observe the sorting logic, higher percentage is sorted first

            return Integer.compare(gas2.getPercentageInAtmo(), gas1.getPercentageInAtmo());
        }

    }
}
