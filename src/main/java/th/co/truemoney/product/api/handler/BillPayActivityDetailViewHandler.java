package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.manager.BillConfigurationManager;
import th.co.truemoney.product.api.util.Utils;

@Component
public class BillPayActivityDetailViewHandler extends
		GeneralActivityDetailViewHandler {
	
	@Autowired
	BillConfigurationManager configurationManager;
	
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
	
	private String formatRef2(String ref2Value) {
		return ref2Value != null ? ref2Value.trim() : "";
	}
	
	private boolean hasRef2(String ref2Value) {
		String ref2 = formatRef2(ref2Value);
		return StringUtils.hasText(ref2) && (!"-".equals(ref2));
	}
	
	private String getTitleTh(String action) {
		return Utils.isTrueCorpBill(action) ? "" : configurationManager.getTitleTh(action);
	}
	
	private String getTitleEn(String action) {
		return Utils.isTrueCorpBill(action) ? "" : configurationManager.getTitleEn(action);
	}
	
	private String getRef1TitleEn(String action) {
		return configurationManager.getRef1TitleEn(action);
	}
	
	private String getRef1TitleTh(String action) {
		return configurationManager.getRef1TitleTh(action);
	}
	
	private String getRef2TitleEn(String action) {
		return configurationManager.getRef2TitleEn(action);
	}
	
	private String getRef2TitleTh(String action) {
		return configurationManager.getRef2TitleTh(action);
	}
	
	public void setConfigurationManager(BillConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
}
