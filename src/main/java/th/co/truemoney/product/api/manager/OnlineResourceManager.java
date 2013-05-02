package th.co.truemoney.product.api.manager;

import org.springframework.stereotype.Component;

@Component
public class OnlineResourceManager {
	
	private static final String imageBankURL = "https://secure.truemoney-dev.com/m/tmn_webview/";
	
	public String getBankLogoURL(String bankCode) {
		String returnData = new String();
		if (bankCode.equals("SCB")) {
			returnData = imageBankURL + "images/logo_bank/scb@2x.png";
		} else if (bankCode.equals("KTB")) {
			returnData = imageBankURL + "images/logo_bank/ktb@2x.png";
		} else if (bankCode.equals("BBL")) {
			returnData = imageBankURL + "images/logo_bank/bbl@2x.png";
		} else if (bankCode.equals("BAY")) {
			returnData = imageBankURL + "images/logo_bank/bay@2x.png";
		} else if (bankCode.equals("KBANK")) {
			returnData = imageBankURL + "images/logo_bank/kbank@2x.png";
		} else if (bankCode.equals("TMB")) {
			returnData = imageBankURL + "images/logo_bank/tmb@2x.png";
		}
		return returnData;
	}
}
