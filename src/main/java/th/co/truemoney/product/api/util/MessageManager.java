package th.co.truemoney.product.api.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
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
    	return messageSource.getMessage(namespace + "." + code, null, TH);
	}
	
	public String getMessageEn(String namespace, String code){
    	return messageSource.getMessage(namespace + "." + code, null, EN);
	}
	
}
