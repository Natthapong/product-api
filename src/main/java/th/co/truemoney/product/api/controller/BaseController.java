package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {
	
	@Autowired
	ProductResponseFactory responseFactory;
	
	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
	ProductResponse handleInvalidParameterExceptions(
    		InvalidParameterException exception, 
			HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return responseFactory.createErrorProductResponse(
				new ServiceInventoryException(
						exception.getMessage(),
						"",
						ProductResponseFactory.PRODUCT_NAMESPACE));
	}
	
	@ExceptionHandler(ServiceInventoryException.class)
	public @ResponseBody
	ProductResponse handleServiceInventoryExceptions(
			ServiceInventoryException exception, 
			HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return responseFactory.createErrorProductResponse(exception);
	}
	
}
