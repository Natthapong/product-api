package th.co.truemoney.product.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.util.Utils;

@Component
public class OnlineResourceManager {

    private static final String LOGO_BILL_URL = "/m/tmn_webview/images/logo_bill/";

    private static final String LOGO_BANK_URL = "/m/tmn_webview/images/logo_bank/";

    private static final String LOGO_ACTIVITY_TYPE_URL = "/m/tmn_webview/images/logo_activity_type/";
    
    @Autowired @Qualifier("apiHost")
	private String apiHost;
    
    public String getBankLogoURL(String bankCode) {
    	return apiHost + LOGO_BANK_URL + lower(bankCode) + "@2x.png";
    }

    public String getActivityTypeLogoURL(String type) {
        return apiHost + LOGO_ACTIVITY_TYPE_URL + Utils.removeSuffix(lower(type)) + ".png";
    }

    public String getActivityActionLogoURL(String action) {
        
        action = Utils.removeSuffix(lower(action));
        action = "tmhtopup".equals(action) ? "tmvh" : action;
        action = "tmvtopup".equals(action)  ? "trmv" : action;
        
        return apiHost + LOGO_BILL_URL + action + "@2x.png";
     }
    
    private String lower(String str) {
    	return str != null ? str.toLowerCase() : "";
    }
    
    public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}
}
