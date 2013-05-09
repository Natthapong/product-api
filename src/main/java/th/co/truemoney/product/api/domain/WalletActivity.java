package th.co.truemoney.product.api.domain;

import java.util.EnumMap;

public class WalletActivity {
	
	private static EnumMap<TYPE, String> representThaiText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private static EnumMap<TYPE, String> representEnglishText = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
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
	}

	public static enum TYPE {
		TOPUP_MOBILE, ADD_MONEY, TRANSFER, BILLPAY, BONUS;
	}

	public static WalletActivity.TYPE getEnum(String s) {
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
		throw new IllegalArgumentException("No Enum specified for this string");
	}
	
	public static String getEnglishText(TYPE type) {
		return representEnglishText.get(type);
	}
	
	public static String getEnglishText(String type) {
		return getEnglishText(getEnum(type));
	}
	
	public static String getThaiText(TYPE type) {
		return representThaiText.get(type);
	}
	
	public static String getThaiText(String type) {
		return getThaiText(getEnum(type));
	}
}
