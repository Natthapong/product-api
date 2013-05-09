package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import th.co.truemoney.product.api.domain.ActivityType;

public class TransferActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = new HashMap<String, String>();
		String transferTxt = "";
		 if (ActivityType.TRANSFER_DEBTOR.equals(activity.getAction())) {
			 transferTxt = ActivityType.TRANSFER_TXT;
		 } else if (ActivityType.TRANSFER_CREDITOR.equals(activity.getAction())) {
			 transferTxt = ActivityType.RECIEVE_TXT;
		 } 
		 section1.put("titleTh", transferTxt);
		 section1.put("titleEn", activity.getAction());
		return section1;
	}

}
