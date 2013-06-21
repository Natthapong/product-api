package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.util.Utils;

@Component
public class TopupMobileActivityDetailViewHandler extends GeneralActivityDetailViewHandler {
	
	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(activity.getAction()));
		section1.put("titleTh", "");
		section1.put("titleEn", "");
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		 Map<String, Object> section2 = super.buildSection2();
		 Map<String, Object> column1 = new HashMap<String, Object>();
		 Map<String, String> cell1 = new HashMap<String, String>();
		 cell1.put("titleTh", "หมายเลขโทรศัพท์");
		 cell1.put("titleEn", "mobile number");
		 cell1.put("value", Utils.formatMobileNumber(activity.getRef1()));
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2; 
	}

}
