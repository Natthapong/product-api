package th.co.truemoney.product.api.manager;

import org.springframework.stereotype.Component;

@Component
public class OnlineResourceManager {
	
	private static final String logoBankURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/";
	
	private static final String logoActivityTypeURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_activity_type/";
	
	public String getBankLogoURL(String bankCode) {
		return logoBankURL + lower(bankCode) + "@2x.png";
	}
	
	private String lower(String str) {
		return str != null ? str.toLowerCase() : "";
	}
	
	public String getLogoActivityTypeURL(String type) {
		return logoActivityTypeURL + lower(type) + ".png";
	}
}
