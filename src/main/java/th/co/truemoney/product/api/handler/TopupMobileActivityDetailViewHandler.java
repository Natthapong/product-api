package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public class TopupMobileActivityDetailViewHandler implements ActivityDetailViewHandler {
	
	private OnlineResourceManager onlineResourceManager = new OnlineResourceManager();
	
	private ActivityDetail activity;
	
	@Override
	public void handle(ActivityDetail activity) {
		this.activity = activity;
	}

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = new HashMap<String, String>();
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(activity.getAction()));
		section1.put("titleTh", "");
		section1.put("titleEn", "");
		return section1;
	}

}
