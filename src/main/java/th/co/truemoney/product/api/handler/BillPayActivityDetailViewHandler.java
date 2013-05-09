package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class BillPayActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {

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
		 Map<String, String> cell2 = new HashMap<String, String>();
		 cell1.put("titleTh", "รหัสลูกค้า");
		 cell1.put("titleEn", "customer ID");
		 cell1.put("value", activity.getRef1());
		 cell2.put("titleTh", "เลขที่ใบแจ้งค่าบริการ");
		 cell2.put("titleEn", "invoice number");
		 cell2.put("value", StringUtils.hasText(activity.getRef2()) ? activity.getRef2() : "-");
		 column1.put("cell2", cell2);
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2;

	}

}
