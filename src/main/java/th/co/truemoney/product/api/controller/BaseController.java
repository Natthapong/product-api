package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {

	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
    Map<String,String> handleInvalidParameterExceptions(InvalidParameterException exception, WebRequest request,
			HttpServletResponse response) {
        Map<String,String> error = new HashMap<String, String>();

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		error.put(ResponseParameter.STATUS, exception.getMessage());
		error.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		return error;
	}
	
	@ExceptionHandler(ServiceInventoryException.class)
	public @ResponseBody
	Map<String,String> handleServiceInventoryExceptions(ServiceInventoryException exception, WebRequest request,
			HttpServletResponse response) {
        Map<String,String> error = new HashMap<String, String>();

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		error.put(ResponseParameter.STATUS, exception.getErrorCode());
		error.put(ResponseParameter.NAMESPACE, exception.getErrorNamespace());
		return error;
	}

}
