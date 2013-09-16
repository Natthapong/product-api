package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.exception.ProductAPIException;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {
	
	@Autowired
	protected ProductResponseFactory responseFactory;
	
	private Logger logger = Logger.getLogger(BaseController.class);
	
	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
	ProductResponse handleInvalidParameterExceptions(
    		InvalidParameterException exception, 
			HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return responseFactory.createErrorProductResponse(
				new ServiceInventoryException(400, exception.getMessage(), "", ProductResponseFactory.PRODUCT_NAMESPACE));
	}
	
	@ExceptionHandler(ProductAPIException.class)
	public @ResponseBody
	ProductResponse handleProductAPIExceptions(
			ProductAPIException exception, 
			HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return responseFactory.createErrorProductResponse(
				new ServiceInventoryException(400, exception.getMessage(), "", ProductResponseFactory.PRODUCT_NAMESPACE));
	}
	
	@ExceptionHandler(ServiceInventoryException.class)
	public @ResponseBody
	ProductResponse handleServiceInventoryExceptions(
			ServiceInventoryException exception, 
			HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		log(exception);
		
		return responseFactory.createErrorProductResponse(exception);
	}
	
	private void log(ServiceInventoryException e) {
		if ("INTERNAL_SERVER_ERROR".equals(e.getErrorNamespace())) {
			String logMsg = new StringBuilder()
				.append("namespace=").append(e.getErrorNamespace()).append(",")
				.append("errcode=").append(e.getErrorCode()).append(",")
				.append("errMsg=").append(e.getErrorDescription()).append(",")
				.append("devMsg=").append(e.getDeveloperMessage()).append(",")
				.toString();
			logger.error(logMsg);
		}
	}

	public void setResponseFactory(ProductResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}
	
}
