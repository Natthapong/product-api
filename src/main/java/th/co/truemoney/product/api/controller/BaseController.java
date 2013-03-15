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
	private MessageManager messageManager;
	
	@ExceptionHandler(InvalidParameterException.class)
	public @ResponseBody
    Map<String,String> handleInvalidParameterExceptions(InvalidParameterException exception, WebRequest request,
			HttpServletResponse response) {
        Map<String,String> error = new HashMap<String, String>();
        
        String namespace = "TMN-PRODUCT";
        String status    = exception.getMessage();
        
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		error.put(ResponseParameter.STATUS, status);
		error.put(ResponseParameter.NAMESPACE, namespace);
		error.put(ResponseParameter.MESSAGE_EN, messageManager.getMessageEn(namespace, status));
		error.put(ResponseParameter.MESSAGE_TH, messageManager.getMessageTh(namespace, status));
		return error;
	}
	
	@ExceptionHandler(ServiceInventoryException.class)
	public @ResponseBody
	Map<String,String> handleServiceInventoryExceptions(ServiceInventoryException exception, WebRequest request,
			HttpServletResponse response) {
        Map<String,String> error = new HashMap<String, String>();
        String namespace = exception.getErrorNamespace();
        String status    = exception.getErrorCode();
        
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		error.put(ResponseParameter.STATUS, status);
		error.put(ResponseParameter.NAMESPACE, namespace);
		error.put(ResponseParameter.MESSAGE_EN, messageManager.getMessageEn(namespace, status));
		error.put(ResponseParameter.MESSAGE_TH, messageManager.getMessageTh(namespace, status));
		return error;
	}

}
