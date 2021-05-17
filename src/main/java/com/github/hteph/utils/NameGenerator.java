package com.github.hteph.utils;

import com.valkryst.generator.MarkovGenerator;
import io.micrometer.core.instrument.util.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NameGenerator {

    public String compose(int numberOfLetters) {
        List<String> trainingNames = List.of(
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
                "sìneag", "sìonag", "teasag", "teàrlag", "ùna", "una");

        FileInputStream fis;
        try {
            fis = new FileInputStream("src/main/resources/RomanFemaleNames.txt");
            String data = IOUtils.toString(fis, StandardCharsets.UTF_8);
            trainingNames = List.of(data.split(" "));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final MarkovGenerator generator = new MarkovGenerator(trainingNames);


            var buggedName =  generator.generateName(numberOfLetters);

            return bugFixedName(buggedName);

    }

    private String bugFixedName(String buggedName) {

        return buggedName.substring(1).substring(0,1).toUpperCase() + buggedName.substring(2);
    }


}
