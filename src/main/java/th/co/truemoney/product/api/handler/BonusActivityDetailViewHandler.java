package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.util.Utils;

@Component
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
		 cell1.put("value", "เติมเงินด้วยบัญชีธนาคาร");
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2;
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = new HashMap<String, Object>();
		Map<String, Object> column31 = new HashMap<String, Object>();
		Map<String, String> cell311 = new HashMap<String, String>();
		
		cell311.put("titleTh", "ยอดเงินเข้า Wallet");
		cell311.put("titleEn", "total amount");
		cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
		column31.put("cell1", cell311);
		section3.put("column1", column31);
		return section3;
	}

}
