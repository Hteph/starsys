package com.github.hteph.starsys;

import com.github.hteph.generators.StarFactory;
import com.github.hteph.generators.StarSystemGenerator;
import com.github.hteph.repository.objects.Planet;
import com.github.hteph.repository.objects.StellarObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class GreetingController {

	@GetMapping("/system")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="random") String name, Model model) {

		var star = StarFactory.get("random",'A',null);
		ArrayList<StellarObject> systemList = StarSystemGenerator.Generator(star);

		boolean hasMoons = hasMoons(systemList);
		model.addAttribute("objects", systemList);
		model.addAttribute("hasMoons", hasMoons);


		return "system";
	}

	private boolean hasMoons(ArrayList<StellarObject> systemList) {
		return systemList.stream()
						 .filter( o -> o instanceof Planet)
						 .anyMatch( p -> ((Planet) p).getMoonList() != null && !((Planet) p).getMoonList().isEmpty());
	}
}