package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

public class AddMoneyActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = new HashMap<String, String>();
		section1.put("logoURL", onlineResourceManager.getBankLogoURL(activity.getRef1()));
		section1.put("titleTh", "บัญชีธนาคาร");
		section1.put("titleEn", "bank account");
		return section1;
	}

}
