package com.github.hteph.repository.objects;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;

import static com.github.hteph.utils.StringUtils.nicefyGasName;

@Data
@Builder
public class AtmosphericGases implements Serializable, Comparable<AtmosphericGases>{

	private static final long serialVersionUID = 1L;

	private String name;
	private int percentageInAtmo;

	@Override
	public String toString() {

	    var niceName = nicefyGasName(name);

	    return niceName + " (" + percentageInAtmo + "%)";
	}

    @Override
    public int compareTo(AtmosphericGases otherGas) {
        return this.getPercentageInAtmo()-otherGas.getPercentageInAtmo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtmosphericGases that = (AtmosphericGases) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
