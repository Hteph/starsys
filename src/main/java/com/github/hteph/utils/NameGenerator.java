package com.github.hteph.utils;


//import com.valkryst.VNameGenerator.generator.MarkovGenerator;
//import com.valkryst.VNameGenerator.markov.MarkovChain;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class NameGenerator {

    public String compose(int numberOfLetters) {
return "Unknown";
    }
/*        String[] trainingNames = new String[]{
                "ailios", "ailisl", "aimil", "aingealag", "anabla", "anna",
                "aoife", "barabal", "baraball", "barabla", "bearnas", "beasag",
                "beathag", "beileag", "beitidh", "beitiris", "beitris",
                "bhioctoria", "brighde", "brìde", "cairistiòna", "cairistìne",
                "cairistìona", "caitir", "caitlin", "caitrìona", "calaminag",
                "catrìona", "ceana", "ceit", "ceiteag", "ceitidh", "ciorsdan",
                "ciorstag", "ciorstaidh", "ciorstan", "cotrìona", "criosaidh",
                "curstag", "curstaidh", "deirdre", "deòiridh", "deònaidh",
                "dior-bhorgàil", "diorbhail", "doileag", "doilidh", "doirin",
                "dolag", "ealasaid", "eamhair", "eilidh", "eimhir", "eiric",
                "eithrig", "eubh", "eubha", "èibhlin", "fionnaghal", "fionnuala",
                "floireans", "flòraidh", "frangag", "giorsail", "giorsal",
                "gormall", "gormlaith", "isbeil", "iseabail", "iseabal",
                "leagsaidh", "leitis", "lili", "liùsaidh", "lucrais", "lìosa",
                "magaidh", "maighread", "mairead", "mairearad", "malamhìn",
                "malmhìn", "marsail", "marsaili", "marta", "milread", "moibeal",
                "moire", "moireach", "muire", "muireall", "màili", "màiri",
                "mòr", "mòrag", "nansaidh", "oighrig", "olibhia", "peanaidh",
                "peigi", "raghnaid", "raodhailt", "raonaid", "raonaild", "rut",
                "seasaìdh", "seonag", "seònaid", "simeag", "siubhan", "siùsaidh",
                "siùsan", "sorcha", "stineag", "sìle", "sìleas", "sìlis", "sìne",
                "sìneag", "sìonag", "teasag", "teàrlag", "ùna", "una"};

        FileInputStream fis;
        try {
            fis = new FileInputStream("src/main/resources/RomanFemaleNames.txt");
            String data = IOUtils.toString(fis, StandardCharsets.UTF_8);
            trainingNames = data.trim().split(" ");
        } catch (FileNotFoundException e) {
            log.error("++++++++Namefile missing in generator error+++++++++++++\n", e);
        }

        final MarkovGenerator generator = new MarkovGenerator(trainingNames);

        var buggedName = generator.generate(numberOfLetters);

        return bugFixedName(buggedName);

    }

    private String bugFixedName(String buggedName) {

        return buggedName.substring(1).substring(0, 1).toUpperCase() + buggedName.substring(2);
    }*/
}
