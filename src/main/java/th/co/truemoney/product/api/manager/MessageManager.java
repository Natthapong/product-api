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

    public static final String UNKNOWN_MSG = "Unknown Message";

    private static final Locale EN = new Locale("en", "US");
    private static final Locale TH = new Locale("th", "TH");

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getTitleTh(String namespace, String code, Object[] params) {
        String title = getMessage("ttl." + namespace + "." + code, params, TH);
        if (UNKNOWN_MSG.equals(title)) {
            return getTitleTh("unknown", "message", params);
        }
        return title;
    }

    public String getTitleEn(String namespace, String code, Object[] params) {
        String title = getMessage("ttl." + namespace + "." + code, params, EN);
        if (UNKNOWN_MSG.equals(title)) {
            return getTitleEn("unknown", "message", params);
        }
        return title;
    }

    public String getMessageTh(String namespace, String code, Object[] params){
        String msg = getMessage("msg." + namespace + "." + code, params, TH);
        if (UNKNOWN_MSG.equals(msg)) {
            return getMessageTh("unknown", "message", params) + " <" + namespace + "." + code + ">";
        }
        return msg;
    }

    public String getMessageEn(String namespace, String code, Object[] params){
        String msg = getMessage("msg." + namespace + "." + code, params, EN);
        if (UNKNOWN_MSG.equals(msg)) {
            return getMessageEn("unknown", "message", params) + " <" + namespace + "." + code + ">";
        }
        return msg;
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
            return UNKNOWN_MSG;
        }
    }

}
