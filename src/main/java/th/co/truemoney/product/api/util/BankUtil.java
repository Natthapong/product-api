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
		banks.put("SCIB.TH", "ธนาคารนครหลวงไทย");
		banks.put("SCIB.EN", "SCIB");		
		banks.put("CIMB.TH", "ธนาคารซีไอเอ็มบี ไทย");
		banks.put("CIMB.EN", "CIMB THAI");	
		banks.put("TBANK.TH", "ธนาคารธนชาติ");
		banks.put("TBANK.EN", "TBANK");		
		banks.put("TMB.TH", "ธนาคารทหารไทย");
		banks.put("TMB.EN", "TMB");				
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
