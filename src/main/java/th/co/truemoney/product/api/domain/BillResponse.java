package th.co.truemoney.product.api.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public class BillResponse {
	
	public static class Builder {
		private OTP  otp;
		private Bill bill;
		private BillPaymentDraft paymentDraft;
		private BillPaymentTransaction paymentTransaction;
		private BigDecimal walletBalance;
		
		public Map<String, Object> buildBillPaymentDetailResponse() {
			Map<String, Object> response = new HashMap<String, Object>();
			if (bill != null) {
				response.put("target", bill.getTarget());
				response.put("logoURL", bill.getLogoURL());
				response.put("titleTh", bill.getTitleTH());
				response.put("titleEn", bill.getTitleEN());
				
				response.put("ref1TitleTh", bill.getRef1TitleTH());
				response.put("ref1TitleEn", bill.getRef1TitleEN());
				response.put("ref1", bill.getRef1());
				
				response.put("ref2TitleTh", bill.getRef2TitleTH());
				response.put("ref2TitleEn", bill.getRef2TitleEN());
				response.put("ref2", bill.getRef2());
				
				response.put("isFavoritable", String.valueOf(bill.isFavoritable()));
				response.put("isFavorited", String.valueOf(bill.isFavorited()));
				
			}
			if (paymentDraft != null) {
				BigDecimal payAmount = paymentDraft.getAmount();
				BigDecimal totalFee = Utils.calculateTotalFee(payAmount, bill.getServiceFee(), bill.getSourceOfFundFees());
				BigDecimal totalAmount = payAmount.add(totalFee);
				
				response.put("amount", payAmount);
				response.put("totalFee", totalFee);
				
				response.put("totalAmount", totalAmount);
				response.put("sourceOfFund", "Wallet"); //TODO Hard code!!!
				
			}

			BillPaymentConfirmationInfo paymentConfirm = paymentTransaction.getConfirmationInfo();
			if (paymentConfirm != null) {
				response.put("transactionID", paymentConfirm.getTransactionID());
				response.put("transactionDate", paymentConfirm.getTransactionDate());
			}
			if (walletBalance != null) {
				response.put("currentEwalletBalance", walletBalance);
			}
			
			return response;
		}
		
		public Map<String, Object> buildBillCreateResponse() {
			Map<String, Object> response = new HashMap<String, Object>();
			if (otp != null) {
				response.put("billID", bill.getID());
				response.put("otpRefCode", otp.getReferenceCode());
				response.put("mobileNumber", otp.getMobileNumber());
			}
			if (paymentDraft != null) {
				BigDecimal payAmount = paymentDraft.getAmount();
				BigDecimal totalFee = Utils.calculateTotalFee(payAmount, bill.getServiceFee(), bill.getSourceOfFundFees());
				BigDecimal totalAmount = bill.getAmount().add(totalFee);
				response.put("totalAmount", totalAmount);
			}
			if (bill != null) {
			}
			return response;
		}
		
		public Map<String, Object> buildBillFavoriteResponse() {
			
			Map<String, Object> response = buildBillInfoResponse();
			
			if (paymentDraft != null) {
				response.put("billPaymentID", paymentDraft.getTransactionID());
				response.put("billPaymentStatus", paymentDraft.getStatus());
			}
			return response;
		}
		
		public Map<String, Object> buildBillInfoResponse() {
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
				response.put("partialPaymentAllow", bill.getPartialPayment());
				
				SourceOfFund[] sofs = bill.getSourceOfFundFees();
				if (sofs != null) {
					List<Map<String, Object>> sofList = new ArrayList<Map<String,Object>>();
					for (SourceOfFund sof : sofs) {
						Map<String, Object> sofFee = new HashMap<String, Object>();
						sofFee.put("sourceType", sof.getSourceType());
						sofFee.put("sourceFee", sof.getFeeRate());
						sofFee.put("sourceFeeType", sof.getFeeRateType());
						sofFee.put("minSourceFeeAmount", sof.getMinFeeAmount());
						sofFee.put("maxSourceFeeAmount", sof.getMaxFeeAmount());
						sofList.add(sofFee);
					}
					response.put("sourceOfFundFee", sofList);					
				}
			}
			return response;
		}
		
		public Builder setOTP(OTP otp) {
			this.otp = otp;
			return this;
		}
		
		public Builder setBill(Bill bill) {
			this.bill = bill;
			return this;
		}
		
		public Builder setPaymentDraft(BillPaymentDraft paymentDraft) {
			this.bill = paymentDraft.getBillInfo();
			this.paymentDraft = paymentDraft;
			return this;
		}
		
		public Builder setPaymentTransaction(BillPaymentTransaction transaction) {
			this.paymentTransaction = transaction;
			this.paymentDraft = transaction.getDraftTransaction();
			this.bill = transaction.getDraftTransaction().getBillInfo();
			return this;
		}
		
		public Builder setWalletBalance(BigDecimal balance) {
			this.walletBalance = balance;
			return this;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
}
