package com.github.hteph.generators;


import com.github.hteph.generators.namegenerator.MarkovGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameGenerators {
    
    public static MarkovGenerator femaleGreekNameGenerator;
    
    public static MarkovGenerator femaleRomanNameGenerator;
    
    static{
        var nameGeneratorInit = new NameGenerators();
        
        nameGeneratorInit.initNameGenerators();
        
    }
    
    private void initNameGenerators() {
    
        String[] femaleGreekNames= new String[1];
    
        URL resource = getClass().getClassLoader().getResource("GreekFemaleNames.txt");
    
        {
            try {
                femaleGreekNames = (new String(Files.readAllBytes(Paths.get(resource.toURI())))).split(" ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    
        femaleGreekNameGenerator = new MarkovGenerator(femaleGreekNames);
    
        String[] femaleRomanNames= new String[1];
    
        URL resourceRoman = getClass().getClassLoader().getResource("GreekFemaleNames.txt");
    
        {
            try {
                femaleRomanNames = (new String(Files.readAllBytes(Paths.get(resourceRoman.toURI())))).split(" ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    
        femaleRomanNameGenerator = new MarkovGenerator(femaleRomanNames);
    }
    
}
