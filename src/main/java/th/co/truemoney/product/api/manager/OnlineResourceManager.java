package th.co.truemoney.product.api.manager;

import org.springframework.stereotype.Component;

@Component
public class OnlineResourceManager {

    private static final String LOGO_BILL_URL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/";

    private static final String LOGO_BANK_URL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/";

    private static final String LOGO_ACTIVITY_TYPE_URL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_activity_type/";
    
    public String getBankLogoURL(String bankCode) {
    	return LOGO_BANK_URL + lower(bankCode) + "@2x.png";
    }

    public String getActivityTypeLogoURL(String type) {
        return LOGO_ACTIVITY_TYPE_URL + removeSuffix(lower(type)) + ".png";
    }

    public String getActivityActionLogoURL(String action) {
        
        action = removeSuffix(lower(action));
        action = "tmvhtopup".equals(action) ? "tmvh" : action;
        action = "tmvtopup".equals(action)  ? "trmv" : action;
        
        return LOGO_BILL_URL + action + "@2x.png";
     }
    
    private String lower(String str) {
    	return str != null ? str.toLowerCase() : "";
    }

    private String removeSuffix(String s) {
    	return s.replace("_c", "");
    }
}
