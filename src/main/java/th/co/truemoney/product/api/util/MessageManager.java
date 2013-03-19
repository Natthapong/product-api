package th.co.truemoney.product.api.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component
public class MessageManager implements MessageSourceAware {
	
	@Autowired
	private MessageSource messageSource;
	
	public static final String UNKNOWN_MESSAGE = "Unknown Message";
	
	public static final String UNKNOWN_HEADER  = "Unknown Header";
	
	private static final Locale EN = new Locale("en", "US");
	private static final Locale TH = new Locale("th", "TH");
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String getHeaderTh(String key, Object[] params) {
		return getHeader(key, params, TH);
	}
	
	public String getHeaderEn(String key, Object[] params) {
		return getHeader(key, params, EN);
	}
	
	private String getHeader(String key, Object[] params, Locale loc) {
		try {
			return messageSource.getMessage(key, params, loc);
		} catch (NoSuchMessageException e) {
			return UNKNOWN_HEADER;
		}
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

}
