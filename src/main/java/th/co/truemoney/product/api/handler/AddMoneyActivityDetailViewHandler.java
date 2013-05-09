package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class AddMoneyActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();
		section1.put("logoURL", onlineResourceManager.getBankLogoURL(activity.getRef1()));
		section1.put("titleTh", "บัญชีธนาคาร");
		section1.put("titleEn", "bank account");
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		Map<String, Object> section2 = super.buildSection2();
		Map<String, Object> column1 = new HashMap<String, Object>();
		Map<String, String> cell1 = new HashMap<String, String>();
		Map<String, String> cell2 = new HashMap<String, String>();
	 	cell1.put("titleTh", "ธนาคาร");
		cell1.put("titleEn", "bank name");
		cell1.put("value", mapBankName(activity.getRef1()));
		cell2.put("titleTh", "หมายเลขบัญชี");
		cell2.put("titleEn", "account number");
		cell2.put("value", StringUtils.hasText(activity.getRef2()) ? activity.getRef2() : "-");
		column1.put("cell2", cell2);
	 	column1.put("cell1", cell1);
	 	section2.put("column1", column1);
		return section2;
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = super.buildSection3();
		Map<String, Object> column2 = (Map<String, Object>)section3.get("column2");
		if (column2.containsKey("cell1")) {
			column2.remove("cell1");
		}
		return section3;
	}
}
