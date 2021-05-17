package com.github.hteph.starsys;

import com.github.hteph.generators.StarFactory;
import com.github.hteph.generators.StarSystemGenerator;
import com.github.hteph.repository.objects.StellarObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

		var star = StarFactory.get("Test",'A',null);
		ArrayList<StellarObject> systemList = StarSystemGenerator.Generator(star);
		model.addAttribute("name", star.getName());
		model.addAttribute("class", star.getClassification());
		model.addAttribute("age", star.getAge());
		model.addAttribute("lumosity", star.getLuminosity());
		model.addAttribute("description", star.getDescription());
		model.addAttribute("mass", star.getMass());
		model.addAttribute("objects", systemList);
		systemList.get(0).getPresentations().getFacts().get("mass");

		return "greeting";
	}

}