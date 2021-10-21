package com.github.hteph.starsys;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class IndexController {

    @RequestMapping("/test")
    public String index(Model model) {

//        var tempPage = new StringBuilder();
//
//        tempPage.append("<!DOCTYPE html>");
//        tempPage.append("<html>");
//        tempPage.append("<body>");
//
//        tempPage.append("<h2>HTML Forms</h2>");
//
//        tempPage.append("<form action=\"/system2\" method=post>");
//        tempPage.append("<label for=\"name\">System name:</label><br>");
//        tempPage.append("<input type=\"text\" id=\"name\" name=\"name\" value=\"Random\"><br>");
//        tempPage.append("<input type=\"checkbox\" id=\"life\" name=\"life\" value=\"yes\">");
//        tempPage.append("<label for=\"vehicle1\">I require life</label><br>");
//        tempPage.append("<input type=\"submit\" value=\"Submit\">");
//        tempPage.append("</form>");
//
//        tempPage.append("<p>If you click the \"Submit\" button, the form-data will be sent to a page called \"/system2\".</p>");
//
//        tempPage.append("</body>");
//        tempPage.append("</html>");
//
//
//        return tempPage.toString();

        return "lcars-index";
    }

}