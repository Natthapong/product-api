package th.co.truemoney.product.api.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Component
public class ProductResponseFactory {
	
	public static final String SUCCESS_CODE = "20000";
	
	public static final String PRODUCT_NAMESPACE = "TMN-PRODUCT";
	
	private static final Object[] EMPTY_PARAMS = new Object[0];
	@Autowired
	MessageManager messageManager;
	
	public ProductResponse createSuccessProductResonse(Map<String, Object> data) {
		ProductResponse success = new ProductResponse();
		success.setCode(SUCCESS_CODE);
		success.setNamespace(PRODUCT_NAMESPACE);
		success.setMessageEn(messageManager.getMessageEn(PRODUCT_NAMESPACE, SUCCESS_CODE, EMPTY_PARAMS));
		success.setMessageTh(messageManager.getMessageTh(PRODUCT_NAMESPACE, SUCCESS_CODE, EMPTY_PARAMS));
		if (data != null && !data.isEmpty())
			success.setData(data);
		return success;
	}
	
	public ProductResponse createErrorProductResponse(String code, String namespace, String description, Object[] params) {
		if (params == null) 
			params = EMPTY_PARAMS;
		ProductResponse error = new ProductResponse();
		error.setCode(code);
		error.setNamespace(namespace);
		error.setMessageEn(messageManager.getMessageEn(namespace, code, params));
		error.setMessageTh(messageManager.getMessageTh(namespace, code, params));
		error.setTitleEn(messageManager.getTitleEn(namespace, code, params));
		error.setTitleTh(messageManager.getTitleTh(namespace, code, params));
		error.setOriginalMessage(description);
		return error;
	}
	
	public ProductResponse createErrorProductResponse(ServiceInventoryException exception) {
		ProductResponse error = new ProductResponse();
		error.setCode(exception.getErrorCode());
		error.setNamespace(exception.getErrorNamespace());
		error.setOriginalMessage(exception.getErrorDescription());
		
		if (isInvalidAmountException(exception)) {
			Map<String, Object> data = exception.getData();
			error.setTitleEn(
				messageManager.getTitleEn(
						exception.getErrorNamespace(), 
						exception.getErrorCode(), 
						new Object[] { data.get("bankNameEn") }));
			error.setTitleTh(
					messageManager.getTitleTh(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), 
							new Object[] { data.get("bankNameTh") }));
			error.setMessageEn(
					messageManager.getMessageEn(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), 
							new Object[] { data.get("minimumAmount"), data.get("maximumAmount") }));
			error.setMessageTh(
					messageManager.getMessageTh(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), 
							new Object[] { data.get("minimumAmount"), data.get("maximumAmount") }));
		} else {
			error.setMessageEn(
					messageManager.getMessageEn(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), EMPTY_PARAMS));
			error.setMessageTh(
					messageManager.getMessageTh(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), EMPTY_PARAMS));
			error.setTitleEn(
					messageManager.getTitleEn(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), EMPTY_PARAMS));
			error.setTitleTh(
					messageManager.getTitleTh(
							exception.getErrorNamespace(), 
							exception.getErrorCode(), EMPTY_PARAMS));
		}
		return error;
	}
	
	private static final String LESS_THAN_MINIMUM_AMOUNT = "20001";
	
	private static final String MORE_THAN_MINIMUM_AMOUNT = "20002";
	
	private boolean isInvalidAmountException(ServiceInventoryException e) {
		return LESS_THAN_MINIMUM_AMOUNT.equals(e.getErrorCode()) || MORE_THAN_MINIMUM_AMOUNT.equals(e.getErrorCode());
	}
}
