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

	@Override
	public Map<String, String> buildSection1() {
		String action = Utils.removeSuffix(activity.getAction());
		boolean truecorpBill = Utils.isTrueCorpBill(action);
		
		Map<String, String> section1 = super.buildSection1();
		section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(action));
		section1.put("titleEn", truecorpBill ? "" : WalletActivity.getActionInEnglish(action));
		section1.put("titleTh", truecorpBill ? "" : WalletActivity.getActionInThai(action));
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		 Map<String, Object> section2 = super.buildSection2();
		 Map<String, Object> column1 = new HashMap<String, Object>();
		 Map<String, String> cell1 = new HashMap<String, String>();
		 Map<String, String> cell2 = new HashMap<String, String>();
		 String action = Utils.removeSuffix(activity.getAction());
		 String ref1TitleTh = "รหัสลูกค้า";
		 String ref1TitleEn = "customer ID";
		 String ref1Value = activity.getRef1();
		 String ref2TitleTh = "เลขที่ใบแจ้งค่าบริการ";
		 String ref2TitleEn = "invoice number";
		 String ref2Value = activity.getRef2();
		 
		 if ("mea".equals(action)) {
			 ref1TitleTh = "บัญชีแสดงสัญญาเลขที่";
			 ref2TitleTh = "เลขที่ใบแจ้งหนี้";
		 } else if ("water".equals(action)) {
			 ref1TitleTh = "รหัสสาขา + ทะเบียนผู้ใช้น้ำ";
			 ref2TitleTh = "เลขที่ใบแจ้งหนี้";
		 }
		 
		 cell1.put("titleTh", ref1TitleTh);
		 cell1.put("titleEn", ref1TitleEn);
		 cell1.put("value", ref1Value);
		 
		 if(ref2Value != "" || ref2Value != null){
			 cell2.put("titleTh", ref2TitleTh);
			 cell2.put("titleEn", ref2TitleEn);
			 cell2.put("value", ref2Value);
		 }
		 
		 column1.put("cell2", cell2);
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2;

	}
	
	

}
