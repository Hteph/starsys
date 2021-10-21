package com.github.hteph.starsys;

import com.github.hteph.generators.CreatureGenerator;
import com.github.hteph.generators.StarFactory;
import com.github.hteph.generators.StarSystemGenerator;
import com.github.hteph.repository.objects.Biosphere;
import com.github.hteph.repository.objects.Creature;
import com.github.hteph.repository.objects.StellarObject;
import com.github.hteph.starsys.utils.RequestSystem;
import com.github.hteph.starsys.utils.ThymeleafUtils;
import com.github.hteph.utils.enums.Breathing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class SystemController {

    @GetMapping("/system")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "random") String name, Model model) {

        boolean findLife = false;
        List<Biosphere> lifeList;
        if(name.equals("life")) findLife = true;
        ArrayList<StellarObject> systemList;

        int safeCount=0;
        do {
            safeCount++;
            var star = StarFactory.get(name, 'A', null);
            systemList = StarSystemGenerator.Generator(star);
            lifeList = ThymeleafUtils.getLife(systemList);

            if(safeCount>20) throw new RuntimeException("No life found");
        }while(findLife && lifeList.isEmpty());

        lifeList.forEach(biosphere -> {
            if(biosphere.getRespiration() != Breathing.PROTO) {
                log.info("This is the Breathing = {}",biosphere.getRespiration());
                biosphere.setCreature(CreatureGenerator.generator(biosphere));
            } else {
                biosphere.setCreature(new Creature(biosphere.getHomeworld(),true));
            }
        });

        model.addAttribute("objects", systemList);
        model.addAttribute("hasMoons", ThymeleafUtils.hasMoons(systemList));
        model.addAttribute("hasLife", lifeList.size() > 0);
        model.addAttribute("biospheres", lifeList);

        return "system";
    }

    @PostMapping(path = "/system2")
    public String systemFacts(@RequestBody String request, Model model){

        Map<String,String> requests = Arrays.stream(request.split("&"))
                                            .map( s -> s.split("="))
                                            .collect(Collectors.toMap( sa-> sa[0], sb ->sb[1]));

        List<Biosphere> lifeList;
        boolean findLife = requests.containsKey("life");
        ArrayList<StellarObject> systemList;

        int safeCount=0;
        do {
            safeCount++;
            var star = StarFactory.get(requests.getOrDefault("name","Schedim"), 'A', null);
            systemList = StarSystemGenerator.Generator(star);
            lifeList = ThymeleafUtils.getLife(systemList);

            if(safeCount>20) throw new RuntimeException("No life found");
        }while(findLife && lifeList.isEmpty());

        lifeList.forEach(biosphere -> {
            if(biosphere.getRespiration() != Breathing.PROTO) {
                biosphere.setCreature(CreatureGenerator.generator(biosphere));
            } else {
                biosphere.setCreature(new Creature(biosphere.getHomeworld(),true));
            }
        });

        model.addAttribute("objects", systemList);
        model.addAttribute("hasMoons", ThymeleafUtils.hasMoons(systemList));
        model.addAttribute("hasLife", lifeList.size() > 0);
        model.addAttribute("biospheres", lifeList);

        return "system2";
    }
}