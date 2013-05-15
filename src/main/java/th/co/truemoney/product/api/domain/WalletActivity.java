package th.co.truemoney.product.api.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

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
		actionList.put("tmvhtopup_c", "TrueMove H");
		actionList.put("tmvtopup_c", "TrueMove");
		actionList.put("catv_c", "True Visions");
		actionList.put("dstv_c", "True Visions");
		actionList.put("tmvh_c", "TrueMove H");
		actionList.put("trmv_c", "TrueMove");
		actionList.put("tlp_c", "TrueLife Plus");
		actionList.put("tr_c", "โทรศัพท์บ้านทรู");
		actionList.put("ti_c", "True Online");
		actionList.put("tic_c", "True 006");
		actionList.put("tcg_c", "บิลกลุ่มทรู");
		actionList.put("mea_c", "การไฟฟ้านครหลวง");
		actionList.put("mmcc", "บัตรเงินสด");
		actionList.put("debtor", "ส่งเงิน");
		actionList.put("debit", "บัญชีธนาคาร");
		actionList.put("creditor", "รับเงิน");
		
		serviceCodeList.put("tmvh_c", 10);
		serviceCodeList.put("trmv_c", 9);
		serviceCodeList.put("tr_c", 8);
		serviceCodeList.put("ti_c", 7);
		serviceCodeList.put("tlp_c", 6);
		serviceCodeList.put("tcg_c", 5);
		serviceCodeList.put("tic_c", 4);
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
		return action; //TODO action description in English add later
	}
	
	public static int getWeightFromServiceCode(String serviceCode){
		return serviceCodeList.containsKey(serviceCode) ? serviceCodeList.get(serviceCode) : 0;
	}
}
