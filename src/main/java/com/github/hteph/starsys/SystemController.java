package com.github.hteph.starsys;

import com.github.hteph.generators.StarFactory;
import com.github.hteph.generators.StarSystemGenerator;
import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.StellarObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SystemController {

    @GetMapping("/system")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "random") String name, Model model) {

        boolean findLife = false;
        List<Biosphere> lifeList;
        if(name.equals("life")) findLife = true;
        ArrayList<StellarObject> systemList;

        do {
            var star = StarFactory.get(name, 'A', null);
            systemList = StarSystemGenerator.Generator(star);
            lifeList = getLife(systemList);

        }while(findLife && lifeList.isEmpty());

        boolean hasMoons = hasMoons(systemList);

        model.addAttribute("objects", systemList);
        model.addAttribute("hasMoons", hasMoons);
        model.addAttribute("hasLife", lifeList.size() > 0);
        model.addAttribute("biospheres", lifeList);


        return "system";
    }

    private List<Biosphere> getLife(ArrayList<StellarObject> systemList) {

        var planetStream = systemList.stream()
                                     .filter(stellarObject -> stellarObject instanceof Planet);

        var moonStream = systemList.stream()
                                   .filter(stellarObject -> stellarObject instanceof Planet)
                                   .map(stellarObject -> ((Planet) stellarObject).getMoonList())
                                   .flatMap(Collection::stream);


        return Stream.concat(planetStream, moonStream)
                     .map(planet -> ((Planet) planet).getLife())
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());

    }

    private boolean hasMoons(ArrayList<StellarObject> systemList) {
        return systemList.stream()
                         .filter(o -> o instanceof Planet)
                         .anyMatch(p -> ((Planet) p).getMoonList() != null && !((Planet) p).getMoonList().isEmpty());
    }
}