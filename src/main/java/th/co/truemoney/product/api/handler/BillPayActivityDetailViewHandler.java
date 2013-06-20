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
			put("fc_en", "Account Number");
			put("fc_th", "หมายเลขบัตรเครดิต");
			put("pb_en", "Account Number");
			put("pb_th", "หมายเลขบัตรเครดิต");
			put("bwc_en", "Customer Number");
			put("bwc_th", "รหัสสาวจำหน่าย");
			put("kcc_en", "Account Number");
			put("kcc_th", "หมายเลขบัตรเครดิต");
			put("uob_en", "Card Number");
			put("uob_th", "หมายเลขบัตร");
			put("mea_en", "Account Number");
			put("mea_th", "บัญชีแสดงสัญญาเลขที่");
			put("ghb_en", "Account Number");
			put("ghb_th", "เลขที่บัญชีเงินกู้");
			put("ktc_en", "Account Number");
			put("ktc_th", "หมายเลขบัตรเครดิต");
			put("bblc_en", "Account Number");
			put("bblc_th", "หมายเลขบัตรเครดิต");
			put("aeon_en", "Ref. 1");
			put("aeon_th", "เลขที่อ้างอิง 1");
			put("water_en", "Account Number");
			put("water_th", "รหัสสาขา + ทะเบียนผู้ใช้น้ำ");
		    put("tisco_en", "Ref. 1");
		    put("tisco_th", "เลขที่อ้างอิง");
		    put("mistine_en", "Customer Number");
		    put("mistine_th", "รหัสสาวจำหน่าย");
		    put("dlt_th", "เลขใบแจ้งชำระค่าภาษี");
		    put("dlt_en", "");
		    put("tli_th", "Ref. No. 1/Cust. No.");
		    put("tli_en", "Ref. No. 1/Cust. No.");
		    put("hiway_th", "เลขที่อ้างอิง");
		    put("hiway_en", "Ref. No. 1");
		    put("mthai_th", "เลขที่กรมธรรม์");
		    put("mthai_en", "Policy Number");
		    put("mti_th", "เลขที่ใบคำขอ");
		    put("mti_en", "Proposal ID");
		    put("ing_th", "หมายเลขอ้างอิง 1");
		    put("ing_en", "Ref. No. 1");
		    put("bkip_th", "กรมธรรม์เลขที่");
		    put("bkip_en", "Policy Number");
		    put("tqmlife_th", "เลขที่อ้างอิง 1");
		    put("tqmlife_en", "Ref. No. 1");
		    put("cimbpe_th", "เลขที่บัญชี");
		    put("cimbpe_en", "");
		    put("cimbex_th", "เลขที่บัญชี");
		    put("cimbex_en", "");
		    put("scal_th", "สัญญาเลขที่");
		    put("scal_en", "Ref. No. 1");
		    put("ask_th", "หมายเลขผู้เช่าซื้อ");
		    put("ask_en", "");
		    put("cal_th", "รหัสลูกค้า (10 หลัก)");
		    put("cal_en", "");
		    put("hp_th", "หมายเลขบัตร");
		    put("hp_en", "Card Number");
		    put("dream_th", "หมายเลขสมาชิก");
		    put("dream_en", "Loan Number");
		    put("robinson_th", "หมายเลขบัญชี");
		    put("robinson_en", "Account Number");
		    put("central_th", "เลขที่บัตรเครดิต (16หลัก)");
		    put("central_en", "");
		    put("centralpl_th", "หมายเลขบัญชี");
		    put("centralpl_en", "Account Number");
		    put("tesco_th", "หมายเลขบัตร");
		    put("tesco_en", "Account Number");
		    put("tescopl_th", "หมายเลขสมาชิก");
		    put("tescopl_en", "Account Number");
		    put("qc_th", "หมายเลขสมาชิก");
		    put("qc_en", "Account Number");
		    put("gepc_th", "เลขที่บัตรเครดิต (16หลัก)");
		    put("gepc_en", "");
		    put("glc_th", "รหัสลูกค้า");
		    put("glc_en", "Customer Number");
		    put("sg_th", "รหัสลูกค้า");
		    put("sg_en", "Customer Number");
		    put("mitt_th", "เลขที่ลูกค้า/รหัสผู้ให้บริการ");
		    put("mitt_en", "Ref. No. 1");
		    put("cigna_th", "รหัสลูกค้า");
		    put("cigna_en", "Customer Number");
		    put("tqm_th", "เลขที่อ้างอิง 1");
		    put("tqm_en", "Ref. No. 1");
		    put("bla_th", "เลขที่อ้างอิง 1");
		    put("bla_en", "Ref. No. 1");
		 }
	};
	
	@SuppressWarnings("serial")
	Map<String,String> ref2Title = new HashMap<String,String>() {
		{
			put("bwc_th", "เลขที่ใบส่งของ");
			put("bwc_en", "Bill Number");
			put("mea_th", "เลขที่ใบแจ้งหนี้");
			put("mea_en", "Ref. 2");
			put("water_th", "เลขที่ใบแจ้งหนี้");
			put("water_en", "Ref. 2");
			put("tisco_th", "สัญญาเลขที่");
			put("tisco_en", "Ref. 2");
		    put("mistine_th", "เลขที่ใบส่งของ");
		    put("mistine_en", "Bill Number");
		    put("dlt_th", "หมายเลขอ้างอิง");
		    put("dlt_en", "");
		    put("tli_th", "Ref. No. 2");
		    put("tli_en", "Ref. No. 2");
		    put("hiway_th", "สัญญาเลขที่");
		    put("hiway_en", "Ref. No. 2");
		    put("mti_th", "เลขที่ตัวแทน");
		    put("mti_en", "Ref. No. 2");
		    put("ing_th", "หมายเลขอ้างอิง 2");
		    put("ing_en", "Ref. No. 2");
		    put("bkip_th", "เลขที่อ้างอิง 2");
		    put("bkip_en", "");
		    put("tqmlife_th", "เลขที่อ้างอิง 2");
		    put("tqmlife_en", "Ref. No. 2");
		    put("cimbpe_th", "วงเงินกู้");
		    put("cimbpe_en", "Ref. 2");
		    put("scal_th", "เลขที่อ้างอิง");
		    put("scal_en", "Ref. No. 2");
		    put("ask_th", "เลขที่สัญญา + ประเภทบริการ");
		    put("ask_en", "");
		    put("cal_th", "เลขที่สัญญา (10 หลัก)");
		    put("cal_en", "");
		    put("glc_th", "หมายเลขอ้างอิง");
		    put("glc_en", "Reference No");
		    put("sg_th", "หมายเลขอ้างอิง");
		    put("sg_en", "Reference No");
		    put("mitt_th", "กรมธรรม์เลขที่/สลักหลังเลขที่");
		    put("mitt_en", "Ref. No. 2");
		    put("cigna_th", "เลขที่อ้างอิง");
		    put("cigna_en", "Reference Number");
		    put("tqm_th", "เลขที่อ้างอิง 2");
		    put("tqm_en", "Ref. No. 2");
		    put("bla_th", "เลขที่อ้างอิง 2");
		    put("bla_en", "Ref. No. 2");
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
