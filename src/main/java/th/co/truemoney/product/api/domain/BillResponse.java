package th.co.truemoney.product.api.domain;

import java.util.HashMap;
import java.util.Map;

import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;

public class BillResponse {
	
	public static class Builder {
		
		private Bill bill;
		
		public Map<String, Object> build() {
			
			Map<String, Object> response = new HashMap<String, Object>();
			if (bill != null) {
				boolean truecorpBill = Utils.isTrueCorpBill(bill.getTarget());
				
				response.put("billID", bill.getID());
				response.put("target", bill.getTarget());
				response.put("logoURL", bill.getLogoURL());
				response.put("titleEn", truecorpBill ? "" : bill.getTitleEN());
				response.put("titleTh", truecorpBill ? "" : bill.getTitleTH());
				response.put("ref1", bill.getRef1());
				response.put("ref1TitleEn", bill.getRef1TitleEN());
				response.put("ref1TitleTh", bill.getRef1TitleTH());
				response.put("ref2", bill.getRef2());
				response.put("ref2TitleEn", bill.getRef2TitleEN());
				response.put("ref2TitleTh", bill.getRef2TitleTH());
				response.put("amount", bill.getAmount());
				response.put("minAmount", bill.getMinAmount());
				response.put("maxAmount", bill.getMaxAmount());
				response.put("serviceFee", bill.getServiceFee().getFeeRate());
				response.put("serviceFeeType", bill.getServiceFee().getFeeRateType());
				response.put("isTrueCorpBill", truecorpBill);
				response.put("partialPaymentAllow", bill.getPartialPayment());
				
				SourceOfFund ew = bill.getEwalletSourceOfFund();
				if (ew != null) {
					Map<String, Object> sofFee = new HashMap<String, Object>();
					sofFee.put("sourceType", ew.getSourceType());
					sofFee.put("sourceFee", ew.getFeeRate());
					sofFee.put("sourceFeeType", ew.getFeeRateType());
					sofFee.put("minSourceFeeAmount", ew.getMinFeeAmount());
					sofFee.put("maxSourceFeeAmount", ew.getMaxFeeAmount());
					
					response.put("sourceOfFundFee", sofFee);					
				}
			}
			return response;
		}

		public Builder setBill(Bill bill) {
			this.bill = bill;
			return this;
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
}
