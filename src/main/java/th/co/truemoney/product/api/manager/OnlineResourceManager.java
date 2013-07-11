package th.co.truemoney.product.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.util.Utils;

@Component
public class OnlineResourceManager {

    private static final String LOGO_BILL_URL = "/images/logo_bill/";

    private static final String LOGO_BANK_URL = "/images/logo_bank/";

    private static final String LOGO_ACTIVITY_TYPE_URL = "/images/logo_activity_type/";
    
    private static final String BG_BANK_IMAGE_URL = "/images/bg_bank_images/";
    
    @Autowired @Qualifier("mHost")
    private String mHost;
    
    public String getBankLogoURL(String bankCode) {
    	return mHost + LOGO_BANK_URL + lower(bankCode) + "@2x.png";
    }

    public String getActivityTypeLogoURL(String type) {
        return mHost + LOGO_ACTIVITY_TYPE_URL + Utils.removeSuffix(lower(type)) + ".png";
    }
    
    public String getBackgroundBankImageURL(String bankCode){
    	return mHost + BG_BANK_IMAGE_URL + lower(bankCode) + "@2x.png";
    }

    public String getActivityActionLogoURL(String action) {
        
        action = Utils.removeSuffix(lower(action));
        action = "tmhtopup".equals(action) ? "tmvh" : action;
        action = "tmvtopup".equals(action)  ? "trmv" : action;
        
        return mHost + LOGO_BILL_URL + action + "@2x.png";
     }
    
    private String lower(String str) {
    	return str != null ? str.toLowerCase() : "";
    }

	public void setmHost(String mHost) {
		this.mHost = mHost;
	}
    
}
