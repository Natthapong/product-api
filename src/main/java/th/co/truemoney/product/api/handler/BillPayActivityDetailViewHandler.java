package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.domain.WalletActivity;
import th.co.truemoney.product.api.util.Utils;

@Component
public class BillPayActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {
	
	@SuppressWarnings("serial")
	Map<String,String> ref1Title = new HashMap<String,String>() {
		{
			put("tr_en", "Account/Mobile Number");
			put("tr_th", "เลขที่อ้างอิง 1/หมายเลขโทรศัพท์");
			put("water_en", "Account Number");
			put("water_th", "รหัสสาขา + ทะเบียนผู้ใช้น้ำ");
			put("mea_en", "Account Number");
			put("mea_th", "บัญชีแสดงสัญญาเลขที่");
			put("bblc_en", "Account Number");
		    put("bblc_th", "หมายเลขบัตรเครดิต");
		    put("tisco_en", "Ref. 1");
		    put("tisco_th", "เลขที่อ้างอิง");
		    put("ghb_en", "Account Number");
		    put("ghb_th", "เลขที่บัญชีเงินกู้");
		    put("ktc_en", "Account Number");
		    put("ktc_th", "หมายเลขบัตรเครดิต");
		    put("aeon_en", "Ref. 1");
		    put("aeon_th", "เลขที่อ้างอิง 1");
		    put("fc_en", "Account Number");
		    put("fc_th", "หมายเลขบัตรเครดิต");
		    put("kcc_en", "Account Number");
		    put("kcc_th", "หมายเลขบัตรเครดิต");
		    put("pb_en", "Account Number");
		    put("pb_th", "หมายเลขบัตรเครดิต");
		    put("uob_en", "Card Number");
		    put("uob_th", "หมายเลขบัตร");
		    put("mistine_en", "Customer Number");
		    put("mistine_th", "รหัสสาวจำหน่าย");
		    put("bwc_en", "Customer Number");
		    put("bwc_th", "รหัสสาวจำหน่าย");
		 }
	};
	
	@SuppressWarnings("serial")
	Map<String,String> ref2Title = new HashMap<String,String>() {
		{
			put("water_en", "Ref. 2");
			put("water_th", "เลขที่ใบแจ้งหนี้");
			put("mea_en", "Ref. 2");
			put("mea_th", "เลขที่ใบแจ้งหนี้");
			put("tisco_en", "Ref. 2");
		    put("tisco_th", "สัญญาเลขที่");
		    put("mistine_en", "Bill Number");
		    put("mistine_th", "เลขที่ใบส่งของ");
		    put("bwc_en", "Bill Number");
		    put("bwc_th", "เลขที่ใบส่งของ");
		}
	};
	
	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();

		String action = Utils.removeSuffix(activity.getAction());
		
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(action));
		section1.put("titleEn", getTitleEn(action));
		section1.put("titleTh", getTitleTh(action));

		return section1;
	}
	
	@Override
	public Map<String, Object> buildSection2() {
		 Map<String, Object> section2 = super.buildSection2();
		 Map<String, Object> column1 = new HashMap<String, Object>();
		 Map<String, String> cell1 = new HashMap<String, String>();
		 section2.put("column1", column1);
		 column1.put("cell1", cell1);
		 
		 String action = Utils.removeSuffix(activity.getAction());

		 cell1.put("titleTh", getRef1TitleTh(action));
		 cell1.put("titleEn", getRef1TitleEn(action));
		 cell1.put("value",   formatRef1(activity.getRef1()));
		 
		 if (hasRef2(activity.getRef2())) {
			 Map<String, String> cell2 = new HashMap<String, String>();
			 cell2.put("titleTh", getRef2TitleTh(action));
			 cell2.put("titleEn", getRef2TitleEn(action));
			 cell2.put("value", formatRef2(activity.getRef2()));
			 column1.put("cell2", cell2);
		 }
		 return section2;
	}
	
	private String formatRef1(String ref1Value) {
		return Utils.formatTelephoneNumber(ref1Value);
	}
	
	private String getTitleTh(String action) {
		return Utils.isTrueCorpBill(action) ? "" : WalletActivity.getActionInThai(action);
	}
	
	private String getTitleEn(String action) {
		return Utils.isTrueCorpBill(action) ? "" : WalletActivity.getActionInEnglish(action);
	}
	
	private String formatRef2(String ref2Value) {
		return ref2Value != null ? ref2Value.trim() : "";
	}
	
	private boolean hasRef2(String ref2Value) {
		String ref2 = formatRef2(ref2Value);
		return StringUtils.hasText(ref2) && (!"-".equals(ref2));
	}
	
	private String getRef1TitleEn(String action) {
		String key = action + "_en";
		return ref1Title.containsKey(key) ? ref1Title.get(key) : "Account/Mobile Number";
	}
	
	private String getRef1TitleTh(String action) {
		String key = action + "_th";
		return ref1Title.containsKey(key) ? ref1Title.get(key) : "รหัสลูกค้า/หมายเลขโทรศัพท์";
	}
	
	private String getRef2TitleEn(String action) {
		String key = action + "_en";
		return ref2Title.containsKey(key) ? ref2Title.get(key) : "invoice number";
	}
	
	private String getRef2TitleTh(String action) {
		String key = action + "_th";
		return ref2Title.containsKey(key) ? ref2Title.get(key) : "เลขที่ใบแจ้งค่าบริการ";
	}	
}
