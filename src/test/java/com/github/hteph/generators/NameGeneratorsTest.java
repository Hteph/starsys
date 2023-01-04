package com.github.hteph.generators;

import com.github.hteph.generators.namegenerator.MarkovGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class NameGeneratorsTest {
    
    
    
    @Test
    void testNameGenerators(){
    
        String[] femaleGreekNames= new String[1];
    
        URL resource = getClass().getClassLoader().getResource("GreekFemaleNames.txt");
        
        {
            try {
                femaleGreekNames = (new String(Files.readAllBytes(Paths.get(resource.toURI())))).split(" ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    
        var femaleGreekNameGenerator = new MarkovGenerator(femaleGreekNames);
        
        var test = femaleGreekNameGenerator.generate(8);
        
        assertNotNull(test);
        
    }
    
    @Test
    void testInitOfNameGenerators(){
       
       var test =  NameGenerators.femaleGreekNameGenerator.generate(8);
       
       assertNotNull(test);
    }
    
}