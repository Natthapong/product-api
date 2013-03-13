package th.co.truemoney.product.api.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import th.co.truemoney.product.api.ResponseParameter;

import javax.servlet.http.HttpServletResponse;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
	
	private static Logger logger = Logger.getLogger(BaseController.class);

	
	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
    Map<String,String> handleAllExceptions(Exception exception, WebRequest request,
			HttpServletResponse response) {
        Map<String,String> error = new HashMap<>();

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		error.put(ResponseParameter.STATUS, exception.getMessage());
                error.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		return error;
	}
	
}
