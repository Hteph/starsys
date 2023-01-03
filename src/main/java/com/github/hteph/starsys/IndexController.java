package com.github.hteph.starsys;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class IndexController {

    @RequestMapping("/test")
    public String index(Model model) {
        

        return "no test currently";
    }

}