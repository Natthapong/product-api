package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

public class BillPayActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = new HashMap<String, String>();
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(activity.getAction()));
		section1.put("titleTh", "");
		section1.put("titleEn", "");
		return section1;
	}

}
