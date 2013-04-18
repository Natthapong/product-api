package th.co.truemoney.product.api.manager;

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
	
	private static final Locale EN = new Locale("en", "US");
	private static final Locale TH = new Locale("th", "TH");
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String getTitleTh(String namespace, String code, Object[] params) {
		return getMessage("ttl." + namespace + "." + code, params, TH);
	}
	
	public String getTitleEn(String namespace, String code, Object[] params) {
		return getMessage("ttl." + namespace + "." + code, params, EN);
	}
	
	public String getMessageTh(String namespace, String code, Object[] params){
		return getMessage("msg." + namespace + "." + code, params, TH);
	}
	
	public String getMessageEn(String namespace, String code, Object[] params){
		return getMessage("msg." + namespace + "." + code, params, EN);
	}
	
	public String getMessageEn(String key) {
		return messageSource.getMessage(key, null, EN);
	}
	
	public String getMessageTh(String key) {
		return messageSource.getMessage(key, null, TH);
	}
	
	private String getMessage(String key, Object[] params, Locale loc) {
		try {
			return messageSource.getMessage(key, params, loc);
		} catch (NoSuchMessageException e) {
			return String.format("%s [%s]", UNKNOWN_MESSAGE, key);
		}
	}

}
