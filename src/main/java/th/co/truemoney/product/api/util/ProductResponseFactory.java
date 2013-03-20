package th.co.truemoney.product.api.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.domain.ProductResponse;

@Component
public class ProductResponseFactory {
	
	public static final String SUCCESS_CODE = "20000";
	
	public static final String PRODUCT_NAMESPACE = "TMN-PRODUCT";
	
	@Autowired
	MessageManager messageManager;
	
	public ProductResponse createSuccessProductResonse(Map<String, Object> data) {
		ProductResponse success = new ProductResponse();
		success.setCode(SUCCESS_CODE);
		success.setNamespace(PRODUCT_NAMESPACE);
		success.setMessageEn(messageManager.getMessageEn(PRODUCT_NAMESPACE, SUCCESS_CODE));
		success.setMessageTh(messageManager.getMessageTh(PRODUCT_NAMESPACE, SUCCESS_CODE));
		if (data != null && !data.isEmpty())
			success.setData(data);
		return success;
	}
	
	public ProductResponse createErrorProductResponse(String code, String namespace, String description) {
		ProductResponse error = new ProductResponse();
		error.setCode(code);
		error.setNamespace(namespace);
		error.setMessageEn(messageManager.getMessageEn(namespace, code));
		error.setMessageTh(messageManager.getMessageTh(namespace, code));
		error.setTitleEn(messageManager.getTitleEn(namespace, code, new Object[0]));
		error.setTitleTh(messageManager.getTitleTh(namespace, code, new Object[0]));
		error.setOriginalMessage(description);
		return error;
	}
}
