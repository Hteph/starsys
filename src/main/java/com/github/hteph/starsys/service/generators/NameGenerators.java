package com.github.hteph.starsys.service.generators;


import com.github.hteph.utils.namegenerator.MarkovGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    
        String[] defaultStringArray={"Phaethon", "Novaeangliae", "Anas", "Alba","Tragelaphus", "Lessonae"};
    
        String[] femaleGreekNames;
    
        URL resource = getClass().getClassLoader().getResource("GreekFemaleNames.txt");
        
            try {
                femaleGreekNames = (new String(Files.readAllBytes(Paths.get(resource.toURI())))).split(" ");
            } catch (Exception e) {
                log.error("Error in creating greek female namne generator");
                femaleGreekNames = defaultStringArray;
            }
    
        femaleGreekNameGenerator = new MarkovGenerator(femaleGreekNames);
    
        String[] femaleRomanNames;
    
        URL resourceRoman = getClass().getClassLoader().getResource("GreekFemaleNames.txt");
    
        
            try {
                femaleRomanNames = (new String(Files.readAllBytes(Paths.get(resourceRoman.toURI())))).split(" ");
            } catch (Exception e) {
                log.error("Error in creating greek female namne generator");
                femaleRomanNames = defaultStringArray;
            }
    
        femaleRomanNameGenerator = new MarkovGenerator(femaleRomanNames);
    }
    
}
