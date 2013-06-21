package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public abstract class GeneralActivityDetailViewHandler implements ActivityDetailViewHandler {
	
	@Autowired
	protected OnlineResourceManager onlineResourceManager;
	
	protected ActivityDetail activity;
	
	@Override
	public void handle(ActivityDetail activity) {
		this.activity = activity;
	}
	
	@Override
	public Map<String, String> buildSection1() {
		return new HashMap<String, String>();
	}

	@Override
	public Map<String, Object> buildSection2() {
		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = new HashMap<String, Object>();
		Map<String, Object> column31 = new HashMap<String, Object>();
		Map<String, Object> column32 = new HashMap<String, Object>();
		Map<String, String> cell311 = new HashMap<String, String>();
		Map<String, String> cell312 = new HashMap<String, String>();
		Map<String, String> cell321 = new HashMap<String, String>();
		cell311.put("titleTh", "จำนวนเงิน");
		cell311.put("titleEn", "amount");
		cell311.put("value", Utils.formatAbsoluteAmount(activity.getAmount()));
		column31.put("cell1", cell311);
		section3.put("column1", column31);
		
		cell312.put("titleTh", "รวมเงินที่ชำระ");
		cell312.put("titleEn", "total amount");
		cell312.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
		cell321.put("titleTh", "ค่าธรรมเนียม");
		cell321.put("titleEn", "total fee");
		cell321.put("value", Utils.formatAbsoluteAmount(activity.getTotalFeeAmount()));
		column32.put("cell1", cell321);
		column31.put("cell2", cell312);
		section3.put("column2", column32);
		
		return section3;
	}
	
	@Override
	public Map<String, Object> buildSection4() {
		Map<String, Object> section4 = new HashMap<String, Object>();
		Map<String, Object> column41 = new HashMap<String, Object>();
		Map<String, Object> column42 = new HashMap<String, Object>();
		Map<String, String> cell411 = new HashMap<String, String>();
		Map<String, String> cell421 = new HashMap<String, String>();
		cell411.put("titleTh", "วันที่-เวลา");
		cell411.put("titleEn", "transaction date");
		cell411.put("value", Utils.formatDateTime(activity.getTransactionDate()));	 
		cell421.put("titleTh", "เลขที่อ้างอิง");
		cell421.put("titleEn", "transaction ID");
		cell421.put("value", activity.getTransactionID());
		column41.put("cell1", cell411);
		column42.put("cell1", cell421);
		section4.put("column1", column41);
		section4.put("column2", column42);
		return section4;
	}
	
	public void setOnlineReourceManager(OnlineResourceManager onlineResourceManager) {
		this.onlineResourceManager = onlineResourceManager;
	}
}
