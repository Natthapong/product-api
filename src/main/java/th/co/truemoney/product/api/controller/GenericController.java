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
	
	@Autowired @Qualifier("mHost")
	private String mHost;
	
	@RequestMapping(value = "/generic", method = RequestMethod.GET)
	public ModelAndView staticGenericUrl(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		
		ModelAndView m = new ModelAndView("forward:/WEB-INF/pages/generic.jsp");
		m.addObject("apiHost", apiHost);
		m.addObject("mHost", mHost);
		return m;
	}

	@RequestMapping(value = "/bill-payment/key-in/list", method = RequestMethod.GET)
	public
	ModelAndView getKeyInBillList(HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		ModelAndView m = new ModelAndView("forward:/WEB-INF/pages/keyin.jsp");
		m.addObject("apiHost", apiHost);
		m.addObject("mHost", mHost);
		return m;
	}
	
	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public void setmHost(String mHost) {
		this.mHost = mHost;
	}
	
}
