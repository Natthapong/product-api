package th.co.truemoney.product.api.util;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component
public class MessageManager implements MessageSourceAware {
	
	@Autowired
	private MessageSource messageSource;
	
	public static final String UNKNOWN_MESSAGE = "Unknown Message Error";
	
	private static final Locale EN = new Locale("en", "US");
	private static final Locale TH = new Locale("th", "TH");
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String getMessageTh(String namespace, String code){
		return getMessage(namespace + "." + code, TH);
	}
	
	public String getMessageEn(String namespace, String code){
		return getMessage(namespace + "." + code, EN);
	}
	
	private String getMessage(String key, Locale loc) {
		try {
			return messageSource.getMessage(key, null, loc);
		} catch (NoSuchMessageException e) {
			return UNKNOWN_MESSAGE;
		}
	}

	public Map<String, Object> mapStatusMessage(Map<String, Object> response) {
		if (response != null) {
			if (response.containsKey(ResponseParameter.STATUS) && response.containsKey(ResponseParameter.NAMESPACE)) {
				String status = (String)response.get(ResponseParameter.STATUS);
				String namespace = (String)response.get(ResponseParameter.NAMESPACE);
				String messageEn = this.getMessageEn(namespace, status);
				String messageTh = this.getMessageTh(namespace, status);
				response.put(ResponseParameter.MESSAGE_EN, messageEn);
				response.put(ResponseParameter.MESSAGE_TH, messageTh);
			} 
		}
		return response;
	}
}
