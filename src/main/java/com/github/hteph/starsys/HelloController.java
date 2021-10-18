package com.github.hteph.starsys;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {

		var tempPage = new StringBuilder();

		tempPage.append("<!DOCTYPE html>");
		tempPage.append("<html>");
		tempPage.append("<body>");

				tempPage.append("<h2>HTML Forms</h2>");

				tempPage.append("<form action=\"/system2\" method=post>" );
				tempPage.append("<label for=\"fname\">First name:</label><br>");
				tempPage.append("<input type=\"text\" id=\"fname\" name=\"fname\" value=\"John\"><br>");
				tempPage.append("<label for=\"lname\">Last name:</label><br>");
				tempPage.append("<input type=\"text\" id=\"lname\" name=\"lname\" value=\"Doe\"><br><br>");
				tempPage.append("<input type=\"submit\" value=\"Submit\">");
				tempPage.append("</form>");

				tempPage.append("<p>If you click the \"Submit\" button, the form-data will be sent to a page called \"/system2\".</p>");

				tempPage.append("</body>");
				tempPage.append("</html>");


		return tempPage.toString();
	}

}