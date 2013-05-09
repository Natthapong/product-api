package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import th.co.truemoney.product.api.domain.ActivityType;

public class BonusActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();
		section1.put("titleTh", "คืนค่าธรรมเนียม");
		section1.put("titleEn", "kickback");
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		 Map<String, Object> section2 = super.buildSection2();
		 Map<String, Object> column1 = new HashMap<String, Object>();
		 Map<String, String> cell1 = new HashMap<String, String>();
		 cell1.put("titleTh", "ทำรายการ");
		 cell1.put("titleEn", "activity");
		 cell1.put("value", ActivityType.DIRECT_DEBIT_ADDMONEY);
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2;
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = super.buildSection3();
		Map<String, Object> column1 = (Map<String, Object>)section3.get("column1");
		if (column1.containsKey("cell2")) {
			column1.remove("cell2");
		}
		if (section3.containsKey("column2")) {
			section3.remove("column2");
		}
		return section3;
	}

}
