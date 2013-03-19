package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {
	
	@Autowired
	protected MessageManager messageManager;
	
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}
	
	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
    Map<String, Object> handleInvalidParameterExceptions(
    		InvalidParameterException exception, 
			HttpServletResponse response) {
		
        Map<String, Object> error = new HashMap<String, Object>();
        
        String namespace = "TMN-PRODUCT";
        String status    = exception.getMessage();
        
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		error.put(ResponseParameter.STATUS, status);
		error.put(ResponseParameter.NAMESPACE, namespace);
		return messageManager.mapStatusMessage(error);
	}
	
	@ExceptionHandler(ServiceInventoryException.class)
	public @ResponseBody
	Map<String, Object> handleServiceInventoryExceptions(
			ServiceInventoryException exception, 
			HttpServletResponse response) {
		
        Map<String, Object> error = new HashMap<String, Object>();
        String namespace = exception.getErrorNamespace();
        String status    = exception.getErrorCode();
        
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		error.put(ResponseParameter.STATUS, status);
		error.put(ResponseParameter.NAMESPACE, namespace);
		return messageManager.mapStatusMessage(error);
	}
	
}
