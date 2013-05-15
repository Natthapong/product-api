package th.co.truemoney.product.api.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;

public final class Utils {

    private static List<String> trueCorpBills = Arrays.asList("catv", "dstv", "tcg", "ti", "tic", "tlp", "tmvh", "tr", "trmv", "true");

    public static Boolean isTrueCorpBill(String target) {
        return trueCorpBills.contains(target);
    }

    public static BigDecimal calculateTotalFee(BigDecimal amount, ServiceFeeInfo serviceFee, SourceOfFund[] sofs) {
        BigDecimal totalFee = BigDecimal.ZERO;
        if (serviceFee != null) {
            totalFee = totalFee.add(serviceFee.calculateFee(amount));
        }

        if (sofs != null) {
            for (SourceOfFund sof : sofs) {
                //TODO warning hard code add fee eWallet only
                if ("EW".equals(sof.getSourceType())) {
                    totalFee = totalFee.add(sof.calculateFee(amount));
                }
            }
        }
        return totalFee;
    }

    public static String formatDate(Date date) {
    	if (date == null) {
    		return "";
    	}
        return new SimpleDateFormat("dd/MM/yy").format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
        	return "";
        }
        return formatDate(date) + " " + new SimpleDateFormat("HH:mm").format(date);
    }

    public static String formatAmount(BigDecimal amount) {
        return new DecimalFormat("##,##0.00").format(amount);
    }

    public static String formatAbsoluteAmount(BigDecimal amount) {
        return formatAmount(amount.abs());
    }

    public static String formatSignedAmount(BigDecimal amount) {
        String formattedAmount = formatAmount(amount);
        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            formattedAmount = "+" + formattedAmount;
        }
        return formattedAmount;
    }

    public static String formatMobileNumber(String mobileNumber) {
        return String.valueOf(mobileNumber).replaceFirst(
                "(\\d{3})(\\d{3})(\\d)", "$1-$2-$3");
    }
}
