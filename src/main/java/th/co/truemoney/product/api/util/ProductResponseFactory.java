package th.co.truemoney.product.api.util;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
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
	
	public ProductResponse createErrorProductResponse(ServiceInventoryException exception) {
		ProductResponse error = new ProductResponse();
		error.setCode(exception.getErrorCode());
		error.setNamespace(exception.getErrorNamespace());
		error.setOriginalMessage(exception.getErrorDescription());
		
		Object[] parameters = null;
		if (isInvalidAmountException(exception)) {
			Map<String, Object> data = exception.getData();
			parameters = new Object[]{ data.get("bankNameEn"), 
								 	   data.get("bankNameTh"), 
								 	   data.get("minAmount"), 
								 	   data.get("maxAmount") };
		} else if (isCallCenterEnabled(exception)) {
			parameters = new Object[]{ messageManager.getMessageEn("call_center_no") };
		} else {
			parameters = EMPTY_PARAMS;
		}
		
		error.setMessageEn(
				messageManager.getMessageEn(
						exception.getErrorNamespace(), 
						exception.getErrorCode(), parameters));
		error.setMessageTh(
				messageManager.getMessageTh(
						exception.getErrorNamespace(), 
						exception.getErrorCode(), parameters));
		error.setTitleEn(
				messageManager.getTitleEn(
						exception.getErrorNamespace(), 
						exception.getErrorCode(), parameters));
		error.setTitleTh(
				messageManager.getTitleTh(
						exception.getErrorNamespace(), 
						exception.getErrorCode(), parameters));
		return error;
	}
	
	private final String LESS_THAN_MINIMUM_AMOUNT = "TMN-SERVICE-INVENTORY.20001";
	
	private final String MORE_THAN_MINIMUM_AMOUNT = "TMN-SERVICE-INVENTORY.20002";
	
	private final String[] CALL_CENTER_ENABLED = new String[]{"TMN-SERVICE-INVENTORY.1006", 
															"TMN-SERVICE-INVENTORY.10002",
															"core.5",
															"core.6",
															"core.7",
															"core.19",
															"core.27",
															"core.38",
															"core.71",
															"core.1012"};
	
	private boolean isInvalidAmountException(ServiceInventoryException e) {
		return LESS_THAN_MINIMUM_AMOUNT.equals(e.getErrorNamespace() + "." + e.getErrorCode()) 
				|| MORE_THAN_MINIMUM_AMOUNT.equals(e.getErrorNamespace() + "." + e.getErrorCode());
	}
	
	private boolean isCallCenterEnabled(ServiceInventoryException e) {
		return Arrays.asList(CALL_CENTER_ENABLED).contains(e.getErrorNamespace() + "." + e.getErrorCode());
	}
	
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}
}
