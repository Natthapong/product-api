package th.co.truemoney.product.api.util;

import java.util.Arrays;
import java.util.List;

public class BillUtil {
	
	private static List<String> trueCorpBills = Arrays.asList("catv", "dstv", "tcg", "ti", "tic", "tlp", "tmvh", "tr", "trmv", "true");
	
	public static Boolean isTrueCorpBill(String target) {
		return trueCorpBills.contains(target);
	}
}
