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
	
	private static final Locale EN = new Locale("en", "US");
	private static final Locale TH = new Locale("th", "TH");
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String getMessageTh(String namespace, String code){
		String message = "";
		try {
			message = messageSource.getMessage(namespace + "." + code, null, TH);
		} catch (NoSuchMessageException e) {
			message = "Unknown Message Error";
		}
		return message;
	}
	
	public String getMessageEn(String namespace, String code){
		String message = "";
		try {
			message = messageSource.getMessage(namespace + "." + code, null, EN);
		} catch (NoSuchMessageException e) {
			message = "Unknown Message Error";
		}
		return message;
	}
	
}
