package th.co.truemoney.product.api.domain;

import java.util.EnumMap;


public class WalletActivity {
	
	
	private EnumMap<TYPE, String> representThaiStrings = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	private EnumMap<TYPE, String> representEnglishStrings = new EnumMap<WalletActivity.TYPE, String>(TYPE.class);
	
	public WalletActivity() {
		super();
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
}
