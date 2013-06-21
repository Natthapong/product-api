package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.util.BankUtil;
import th.co.truemoney.product.api.util.Utils;

@Component
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
		cell1.put("value", BankUtil.getThaiBankName(activity.getRef1()));
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
