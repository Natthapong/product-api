package th.co.truemoney.product.api.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import th.co.truemoney.product.api.util.Utils;

public class WalletActivity {
	
	private static EnumMap<TYPE, String> representThaiText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private static EnumMap<TYPE, String> representEnglishText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private static Map<String, String> actionList = new HashMap<String, String>();
	
	private static Map<String, Integer> serviceCodeList = new HashMap<String, Integer>();
	
	static {
		representThaiText.put(TYPE.TOPUP_MOBILE, "เติมเงินมือถือ");
		representThaiText.put(TYPE.ADD_MONEY, "เติมเงิน Wallet");
		representThaiText.put(TYPE.TRANSFER, "โอนเงิน");
		representThaiText.put(TYPE.BILLPAY, "จ่ายบิล");
		representThaiText.put(TYPE.BONUS, "โปรโมชั่น");
		
		representEnglishText.put(TYPE.TOPUP_MOBILE, "Topup Mobile");
		representEnglishText.put(TYPE.ADD_MONEY, "Add Money");
		representEnglishText.put(TYPE.TRANSFER, "Transfer");
		representEnglishText.put(TYPE.BILLPAY, "Bill Payment");
		representEnglishText.put(TYPE.BONUS, "Promotion");
		
		actionList.put("promo_direct_debit", "คืนค่าธรรมเนียม");
		actionList.put("tmhtopup", "ทรูมูฟ เอช");
		actionList.put("tmvtopup", "ทรูมูฟ");
		actionList.put("catv", "ทรูวิชั่นส์");
		actionList.put("dstv", "ทรูวิชั่นส์");
		actionList.put("tmvh", "ทรูมูฟ เอช");
		actionList.put("trmv", "ทรูมูฟ");
		actionList.put("tlp", "ทรูไลฟ์ พลัส");
		actionList.put("tr", "โทรศัพท์บ้านทรู");
		actionList.put("ti", "ทรูออนไลน์");
		actionList.put("tic", "ทรู 006");
		actionList.put("tcg", "บิลกลุ่มทรู");
		actionList.put("mea", "การไฟฟ้านครหลวง");
		actionList.put("water", "การประปานครหลวง");
		actionList.put("mmcc", "บัตรเงินสด");
		actionList.put("debtor", "ส่งเงิน");
		actionList.put("debit", "บัญชีธนาคาร");
		actionList.put("creditor", "รับเงิน");
		
		serviceCodeList.put("tmvh", 10);
		serviceCodeList.put("trmv", 9);
		serviceCodeList.put("tr", 8);
		serviceCodeList.put("ti", 7);
		serviceCodeList.put("tlp", 6);
		serviceCodeList.put("tcg", 5);
		serviceCodeList.put("tic", 4);
	}

	public static enum TYPE {
		TOPUP_MOBILE, // top up mobile number using wallet
		ADD_MONEY, // add money to wallet
		TRANSFER, // transfer money to/from other wallet
		BILLPAY, // bill payment using wallet
		BONUS; // kick back money to wallet
	}
	
	public static WalletActivity.TYPE getType(String s) {
		if (TYPE.TOPUP_MOBILE.name().equalsIgnoreCase(s)) {
			return TYPE.TOPUP_MOBILE;
		} else if (TYPE.ADD_MONEY.name().equalsIgnoreCase(s)) {
			return TYPE.ADD_MONEY;
		} else if (TYPE.TRANSFER.name().equalsIgnoreCase(s)) {
			return TYPE.TRANSFER;
		} else if (TYPE.BILLPAY.name().equalsIgnoreCase(s)) {
			return TYPE.BILLPAY;
		} else if (TYPE.BONUS.name().equalsIgnoreCase(s)) {
			return TYPE.BONUS;
		}
		throw new IllegalArgumentException("No Enum specified for '" + s + "'");
	}
	
	public static String getTypeInEnglish(TYPE type) {
		return representEnglishText.get(type);
	}
	
	public static String getTypeInEnglish(String type) {
		return getTypeInEnglish(getType(type));
	}
	
	public static String getTypeInThai(TYPE type) {
		return representThaiText.get(type);
	}
	
	public static String getTypeInThai(String type) {
		return getTypeInThai(getType(type));
	}
	
	public static String getActionInThai(String action) {
		action = Utils.removeSuffix(action);
		return actionList.containsKey(action) ? actionList.get(action) : "-";
	}
	
	public static String getActionInEnglish(String action) {
		return action; //TODO action description in English add later
	}
	
	public static int getWeightFromServiceCode(String serviceCode){
		serviceCode = Utils.removeSuffix(serviceCode);
		return serviceCodeList.containsKey(serviceCode) ? serviceCodeList.get(serviceCode) : 0;
	}
}
