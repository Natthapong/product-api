package th.co.truemoney.product.api.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class WalletActivity {
	
	private static EnumMap<TYPE, String> representThaiText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private static EnumMap<TYPE, String> representEnglishText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private static Map<String, String> actionList = new HashMap<String, String>();
	
	public WalletActivity() {
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
		actionList.put("d.tmvhtopup", "TrueMove H");
		actionList.put("d.tmvtopup", "TrueMove");
		actionList.put("d.catv", "True Visions");
		actionList.put("d.dstv", "True Visions");
		actionList.put("d.tmvh", "TrueMove H");
		actionList.put("d.trmv", "TrueMove");
		actionList.put("d.tlp", "Truelifeplus");
		actionList.put("d.tr", "โทรศัพท์บ้านทรู");
		actionList.put("d.ti", "True Online");
		actionList.put("d.tic", "True 006");
		actionList.put("d.tcg", "บิลกลุ่มทรู");
		actionList.put("mmcc", "บัตรเงินสด");
		actionList.put("debtor", "ส่งเงิน");
		actionList.put("debit", "บัญชีธนาคาร");
		actionList.put("creditor", "รับเงิน");
	}

	public static enum TYPE {
		TOPUP_MOBILE, ADD_MONEY, TRANSFER, BILLPAY, BONUS;
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
		return actionList.containsKey(action) ? actionList.get(action) : "-";
	}
	
	public static String getActionInEnglish(String action) {
		return getActionInThai(action); //TODO action description in English add later
	}
}
