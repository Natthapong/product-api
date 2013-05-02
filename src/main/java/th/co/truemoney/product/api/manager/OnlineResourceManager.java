package th.co.truemoney.product.api.manager;

import org.springframework.stereotype.Component;

@Component
public class OnlineResourceManager {
	
	private static final String logoBillURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/";

	private static final String logoBankURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/";
	
	private static final String logoActivityTypeURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_activity_type/";
	
	public String getBankLogoURL(String bankCode) {
		return logoBankURL + lower(bankCode) + "@2x.png";
	}
	
	private String lower(String str) {
		return str != null ? str.toLowerCase() : "";
	}
	
	public String getActivityTypeLogoURL(String type) {
		return logoActivityTypeURL + lower(type) + ".png";
	}
	
	public String getActivityActionLogoURL(String action) {
		if (action == null) return ""; 
		
		String logoName = "";
		String[] splitted = action.split("\\.");
		if ("tmvhtopup".equals(splitted[1])) {
			logoName = "tmvh";
		} else if ("tmvtopup".equals(splitted[1])) {
			logoName = "trmv";
		} else {
			logoName = splitted[1];
		}
		return logoBillURL + logoName + "@2x.png";
	 }
}
