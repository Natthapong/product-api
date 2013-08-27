package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import th.co.truemoney.product.api.util.Utils;

@Component
public class BuyCashCardActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {
	
	@Override
	public Map<String, String> buildSection1() {
		String action = Utils.removeSuffix(activity.getAction());
		Map<String, String> section1 = super.buildSection1();
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(action));
		section1.put("titleTh", "");
		section1.put("titleEn", "");
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		Map<String, Object> section2 = super.buildSection2();
		Map<String, Object> column21 = new HashMap<String, Object>();
		Map<String, String> cell211 = new HashMap<String, String>();
		Map<String, String> cell212 = new HashMap<String, String>();
		cell211.put("titleTh", "ผู้รับบัตรเงินสด");
		cell211.put("titleEn", "recipient mobile number");
		cell211.put("value", Utils.formatMobileNumber(activity.getRef1()));
		column21.put("cell1", cell211);
		section2.put("column1", column21);
		
		cell212.put("titleTh", "รหัสเติมเงิน");
		cell212.put("titleEn", "cash card code");
		cell212.put("value", activity.getAdditionalData());
		column21.put("cell2", cell212);
		return section2;
	}


	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = new HashMap<String, Object>();
		Map<String, Object> column31 = new HashMap<String, Object>();
		Map<String, String> cell311 = new HashMap<String, String>();
		Map<String, String> cell312 = new HashMap<String, String>();
		cell311.put("titleTh", "จำนวนเงิน");
		cell311.put("titleEn", "amount");
		cell311.put("value", Utils.formatAbsoluteAmount(activity.getAmount()));
		column31.put("cell1", cell311);
		section3.put("column1", column31);
		
		cell312.put("titleTh", "เลขที่บัตรเงินสดทรูมันนี่");
		cell312.put("titleEn", "cash card serial number");
		cell312.put("value", this.activity.getRef2());
		column31.put("cell2", cell312);
		return section3;
	}
	
}
