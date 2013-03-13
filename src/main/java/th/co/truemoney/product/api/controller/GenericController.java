package th.co.truemoney.product.api.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GenericController {
	@RequestMapping(value = "/generic", method = RequestMethod.GET)
	public String staticGenericUrl(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
    	return "forward:/WEB-INF/content/generic.json";
	}
}
