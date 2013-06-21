package th.co.truemoney.product.api.util;

import java.util.HashMap;
import java.util.Map;

public class BankUtil {
	
	private static Map<String, String> banks = new HashMap<String, String>();
	
	static {
		banks.put("KTB.TH", "ธนาคารกรุงไทย");
		banks.put("KTB.EN", "Krung Thai Bank");
		banks.put("SCB.TH", "ธนาคารไทยพาณิชย์");
		banks.put("SCB.EN", "Siam Commercial Bank");
		banks.put("BBL.TH", "ธนาคารกรุงเทพ");
		banks.put("BBL.EN", "Bangkok Bank");
		banks.put("BAY.TH", "ธนาคารกรุงศรีอยุธยา");
		banks.put("BAY.EN", "Bank Of Ayudhaya");
	}
	
	public static String getThaiBankName(String bankCode) {
		return banks.get(upper(bankCode) + ".TH");
	}

	public static String getEnglishBankName(String bankCode) {
		return banks.get(upper(bankCode) + ".EN");
	}
	
	private static String upper(String s) {
		return s != null ? s.toUpperCase() : "";
	}
	
}
