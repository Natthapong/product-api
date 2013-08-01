package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.util.BankUtil;
import th.co.truemoney.product.api.util.Utils;

@Component
public class AddMoneyActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

	private static final String SOF_TMCC = "tmcc";
	private static final Long KIOSK_CHANNEL = 33l;
	private static final Long CP_FRESH_MART_CHANNEL = 38l;
	private static final Long TMX_CHANNEL = 39l;
	private static final Long IOS_APP_CHANNEL = 40l;
	private static final Long ATM_CHANNEL = 42l;
	private static final Long IBANKING = 43l;
	private static final Long TRM_CHANNEL = 44l;

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();
		Long channel = activity.getChannel();
		String action  = activity.getAction();
		if (SOF_TMCC.equals(action)) {
			section1.put("titleTh", "บัตรเงินสดทรูมันนี่");
			section1.put("titleEn", "True Money Cash Card");
		} else if (KIOSK_CHANNEL.equals(channel)) {
			section1.put("titleTh", "True Money Kiosk");
			section1.put("titleEn", "True Money Kiosk");
		} else if (CP_FRESH_MART_CHANNEL.equals(channel)) {
			section1.put("titleTh", "CP Fresh Mart");
			section1.put("titleEn", "CP Fresh Mart");
		} else if (TMX_CHANNEL.equals(channel)) {
			section1.put("titleTh", "True Money Express");
			section1.put("titleEn", "True Money Express");		
		} else if (IOS_APP_CHANNEL.equals(channel)) {
			section1.put("logoURL", onlineResourceManager.getBankLogoURL(activity.getRef1()));
			section1.put("titleTh", "บัญชีธนาคาร");
			section1.put("titleEn", "bank account");
		} else if (ATM_CHANNEL.equals(channel)) {
			section1.put("titleTh", "เอทีเอ็ม");
			section1.put("titleEn", "ATM");			
		} else if (IBANKING.equals(channel)) {
			section1.put("titleTh", "ไอแบงก์กิ้ง");
			section1.put("titleEn", "iBanking");			
		} else if (TRM_CHANNEL.equals(channel)) {
			section1.put("titleTh", "True Shop");
			section1.put("titleEn", "True Shop");			
		} else {
			section1.put("logoURL", onlineResourceManager.getBankLogoURL(activity.getRef1()));
			section1.put("titleTh", "บัญชีธนาคาร");
			section1.put("titleEn", "bank account");
		}
		return section1;
	}

	@Override
	public Map<String, Object> buildSection2() {
		Map<String, Object> section2 = super.buildSection2();
		Long channel = activity.getChannel();
		String action  = activity.getAction();
		if (SOF_TMCC.equals(action)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
		 	cell1.put("titleTh", "serial บัตรเงินสด");
			cell1.put("titleEn", "serial number");
			cell1.put("value", activity.getRef1());
		 	column1.put("cell1", cell1);
		 	section2.put("column1", column1);
		} else if (KIOSK_CHANNEL.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
			cell1.put("titleTh", "ยอดเงินเข้า Wallet");
			cell1.put("titleEn", "total amount");
			cell1.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column1.put("cell1", cell1);
			section2.put("column1", column1);	 	
		} else if (CP_FRESH_MART_CHANNEL.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
		 	cell1.put("titleTh", "หมายเลขสาขา");
			cell1.put("titleEn", "หมายเลขสาขา");
			cell1.put("value", activity.getRef1());
		 	column1.put("cell1", cell1);
		 	section2.put("column1", column1);
		} else if (TMX_CHANNEL.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
		 	cell1.put("titleTh", "หมายเลขร้านค้า");
			cell1.put("titleEn", "หมายเลขร้านค้า");
			cell1.put("value", activity.getRef1());
		 	column1.put("cell1", cell1);
		 	section2.put("column1", column1);
		} else if (IOS_APP_CHANNEL.equals(channel)) {
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
		} else if (ATM_CHANNEL.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
		 	cell1.put("titleTh", "ธนาคาร");
			cell1.put("titleEn", "bank name");
			cell1.put("value", BankUtil.getThaiBankName(activity.getRef1()));
		 	column1.put("cell1", cell1);
		 	section2.put("column1", column1);
		} else if (IBANKING.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
		 	cell1.put("titleTh", "ธนาคาร");
			cell1.put("titleEn", "bank name");
			cell1.put("value", BankUtil.getThaiBankName(activity.getRef1()));
		 	column1.put("cell1", cell1);
		 	section2.put("column1", column1);
		} else if (TRM_CHANNEL.equals(channel)) {
			Map<String, Object> column1 = new HashMap<String, Object>();
			Map<String, String> cell1 = new HashMap<String, String>();
			cell1.put("titleTh", "ยอดเงินเข้า Wallet");
			cell1.put("titleEn", "total amount");
			cell1.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column1.put("cell1", cell1);
			section2.put("column1", column1);	
		} else {
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
		}
		return section2;
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = new HashMap<String, Object>();
		Long channel = activity.getChannel();
		String action  = activity.getAction();
		if (SOF_TMCC.equals(action)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, Object> column32 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			Map<String, String> cell312 = new HashMap<String, String>();
			Map<String, String> cell321 = new HashMap<String, String>();
			cell311.put("titleTh", "จำนวนเงิน");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);			
			cell312.put("titleTh", "ยอดเงินเข้า Wallet");
			cell312.put("titleEn", "amount");
			cell312.put("value", Utils.formatAbsoluteAmount(activity.getAmount()));
			cell321.put("titleTh", "ค่าธรรมเนียม");
			cell321.put("titleEn", "total fee");
			cell321.put("value", Utils.formatAbsoluteAmount(activity.getTotalFeeAmount()));
			column32.put("cell1", cell321);
			column31.put("cell2", cell312);
			section3.put("column2", column32);
		} else if (KIOSK_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, Object> column32 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			Map<String, String> cell321 = new HashMap<String, String>();
			cell311.put("titleTh", "วันที่-เวลา");
			cell311.put("titleEn", "transaction date");
			cell311.put("value", Utils.formatDateTime(activity.getTransactionDate()));	 
			cell321.put("titleTh", "เลขที่อ้างอิง");
			cell321.put("titleEn", "transaction ID");
			cell321.put("value", activity.getTransactionID());
			column31.put("cell1", cell311);
			column32.put("cell1", cell321);
			section3.put("column1", column31);
			section3.put("column2", column32);
		} else if (CP_FRESH_MART_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		} else if (TMX_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		} else if (IOS_APP_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		} else if (ATM_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		} else if (IBANKING.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		} else if (TRM_CHANNEL.equals(channel)) {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, Object> column32 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			Map<String, String> cell321 = new HashMap<String, String>();
			cell311.put("titleTh", "วันที่-เวลา");
			cell311.put("titleEn", "transaction date");
			cell311.put("value", Utils.formatDateTime(activity.getTransactionDate()));	 
			cell321.put("titleTh", "เลขที่อ้างอิง");
			cell321.put("titleEn", "transaction ID");
			cell321.put("value", activity.getTransactionID());
			column31.put("cell1", cell311);
			column32.put("cell1", cell321);
			section3.put("column1", column31);
			section3.put("column2", column32);
		} else {
			Map<String, Object> column31 = new HashMap<String, Object>();
			Map<String, String> cell311 = new HashMap<String, String>();
			cell311.put("titleTh", "ยอดเงินเข้า Wallet");
			cell311.put("titleEn", "total amount");
			cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
			column31.put("cell1", cell311);
			section3.put("column1", column31);
		}
		return section3;
	}
	
	@Override
	public Map<String, Object> buildSection4() {
		Long channel = activity.getChannel();
		if (TRM_CHANNEL.equals(channel) || KIOSK_CHANNEL.equals(channel)) {
			return new HashMap<String, Object>();
		} else {
			return super.buildSection4();
		}
	}
	
}
