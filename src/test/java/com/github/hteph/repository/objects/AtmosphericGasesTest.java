package com.github.hteph.repository.objects;

import com.github.hteph.utils.StreamUtilities;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AtmosphericGasesTest {
    
    @Test
    void testOfGases() {
        
        var gas1 = AtmosphericGases.builder().name("O2").percentageInAtmo(10).build();
        var gas2 = AtmosphericGases.builder().name("N2").percentageInAtmo(11).build();
        var gas3 = AtmosphericGases.builder().name("H2").percentageInAtmo(12).build();
        var gas4 = AtmosphericGases.builder().name("O2").percentageInAtmo(13).build();
        
        
        Set<AtmosphericGases> gasSet = new HashSet<>();
        
        gasSet.add(gas1);
        gasSet.add(gas2);
        gasSet.add(gas3);
        gasSet.add(gas4);
        
        assert (gasSet.size() == 3);
        
        Map<String, AtmosphericGases> atmoMap = gasSet
                .stream()
                .collect(Collectors.toMap(AtmosphericGases::getName, x -> x));
        
        var maxGas = StreamUtilities.findMaxInMap(atmoMap);
        
        assertTrue(maxGas.isPresent());
        assert (maxGas.get().getPercentageInAtmo() == 12);
    }
}