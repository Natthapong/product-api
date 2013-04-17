package th.co.truemoney.product.api.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;

public class BillUtil {
	
	private static List<String> trueCorpBills = Arrays.asList("catv", "dstv", "tcg", "ti", "tic", "tlp", "tmvh", "tr", "trmv", "true");
	
	public static Boolean isTrueCorpBill(String target) {
		return trueCorpBills.contains(target);
	}
	
	public static BigDecimal calculateTotalFee(BigDecimal amount, ServiceFeeInfo serviceFee, BillPaySourceOfFund[] sofs) {
		BigDecimal totalFee = BigDecimal.ZERO;
		if (serviceFee != null) {
			totalFee.add(serviceFee.calculateFee(amount));
		}
		
		for (BillPaySourceOfFund sof : sofs) {
			totalFee.add(sof.calculateFee(amount));
		}
		return totalFee;
	}
}
