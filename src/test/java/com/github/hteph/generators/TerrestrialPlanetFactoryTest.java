package com.github.hteph.generators;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TerrestrialPlanetFactoryTest {
    
    @Test
    void testPlanetGeneration(){
        
        var star = StarFactory.get("Test", 'A', null);
        
        var test = TerrestrialPlanetFactory.generate("TestID", "Test", "A test Planet", "Test", new BigDecimal(5), 'T', star , 0.0);
        
        assertNotNull(test);
        
    }
}