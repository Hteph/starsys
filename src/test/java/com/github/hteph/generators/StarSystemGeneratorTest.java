package com.github.hteph.generators;

import com.github.hteph.starsys.service.generators.StarFactory;
import com.github.hteph.starsys.service.generators.StarSystemGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StarSystemGeneratorTest {
    
    @Test
    void getSystem() {
        
        
        var testSystem = StarSystemGenerator.getSystem(StarFactory.get("Test", 'A', null ));
        
        assertNotNull(testSystem);
        
    }
}