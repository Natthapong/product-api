package th.co.truemoney.product.api.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GenericController {

	@Autowired @Qualifier("apiHost")
	private String apiHost;

	@RequestMapping(value = "/generic", method = RequestMethod.GET)
	public ModelAndView staticGenericUrl(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		return new ModelAndView("forward:/WEB-INF/pages/generic.jsp").addObject("apiHost", apiHost);
	}

	@RequestMapping(value = "/bill-payment/key-in/list", method = RequestMethod.GET)
	public
	ModelAndView getKeyInBillList(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		return new ModelAndView("forward:/WEB-INF/pages/keyin.jsp").addObject("apiHost", apiHost);
	}
	
	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}
}
