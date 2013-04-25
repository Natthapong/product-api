package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.exception.ProductAPIException;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@RequestMapping(value = "/topup/mobile")
@Controller
public class TopupMobileController extends BaseController {

	@Autowired
	private TopUpMobileService topUpMobileService;
	
	@Autowired
	TmnProfileService profileService;

	private static final BigDecimal topupMinAmount = new BigDecimal(10);
	private static final BigDecimal topupMaxAmount = new BigDecimal(1000);

	@RequestMapping(value = "/draft/verifyAndCreate/{accessTokenID}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse verifyAndCreate(@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {
		String mobileNumber = request.get("mobileNumber");

		String amount = request.get("amount");

		if (!ValidateUtil.checkMobileNumber(mobileNumber)) {
			throw new InvalidParameterException("40002");
		}

		if (ValidateUtil.isEmpty(amount)) {
			throw new InvalidParameterException("60000");
		}

		checkTopupAmount(new BigDecimal(amount));

		TopUpMobileDraft draft = topUpMobileService
				.verifyAndCreateTopUpMobileDraft(mobileNumber, new BigDecimal(
						amount), accessTokenID);
		TopUpMobile topUpMobileInfo = draft.getTopUpMobileInfo();
		BigDecimal topUpAmount = topUpMobileInfo.getAmount();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("draftTransactionID", draft.getID());

		data.put("logoURL", topUpMobileInfo.getLogo());
		data.put(
				"mobileNumber",
				String.valueOf(topUpMobileInfo.getMobileNumber()).replaceFirst(
						"(\\d{3})(\\d{3})(\\d)", "$1-$2-$3"));
		data.put("amount", topUpMobileInfo.getAmount());
		data.put("fee", topUpMobileInfo.getServiceFee().getFeeRate());
		data.put(
				"totalAmount",
				calculateTotalAmount(topUpAmount, topUpMobileInfo
						.getServiceFee().calculateFee(topUpAmount),
						getEwalletSOF(topUpMobileInfo.getSourceOfFundFees())
								.calculateFee(topUpAmount)));

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/sendotp/{draftTransactionID}/{accessTokenID}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse sendOTP(@PathVariable String accessTokenID,
			@PathVariable String draftTransactionID) {
		OTP otp = topUpMobileService.sendOTP(draftTransactionID, accessTokenID);

		TopUpMobileDraft draft = topUpMobileService.getTopUpMobileDraftDetail(
				draftTransactionID, accessTokenID);
		TopUpMobile topUpMobileInfo = draft.getTopUpMobileInfo();
		BigDecimal topUpAmount = topUpMobileInfo.getAmount();

		Map<String, Object> data = new HashMap<String, Object>();
        data.put("mobileNumber", otp.getMobileNumber());
		data.put("refCode", otp.getReferenceCode());
		data.put("totalAmount",
				calculateTotalAmount(topUpAmount, topUpMobileInfo
						.getServiceFee().calculateFee(topUpAmount),
						getEwalletSOF(topUpMobileInfo.getSourceOfFundFees())
								.calculateFee(topUpAmount)));

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/confirm/{draftTransactionID}/{accessTokenID}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse confirmOTP(@PathVariable String draftTransactionID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {
		
		OTP otp = new OTP();
		otp.setMobileNumber(request.get("mobileNumber"));
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("refCode"));

		TopUpMobileDraft.Status status = topUpMobileService.confirmTopUpMobile(
				draftTransactionID, otp, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/{transactionID}/status/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getTopUpMobileStatus(@PathVariable String transactionID,
			@PathVariable String accessTokenID) {

		TopUpMobileTransaction.Status status = topUpMobileService.getTopUpMobileStatus(transactionID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/{transactionID}/details/{accessTokenID}", method = RequestMethod.GET)
		@ResponseBody
		public ProductResponse getTopUpMobileDetails(@PathVariable String transactionID,
				@PathVariable String accessTokenID) {
			
			TopUpMobileTransaction transaction = topUpMobileService.getTopUpMobileResult(transactionID, accessTokenID);
			TopUpMobile topUpMobileInfo = transaction.getDraftTransaction().getTopUpMobileInfo();
			BigDecimal topUpAmount = topUpMobileInfo.getAmount();
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("mobileNumber", transaction.getDraftTransaction().getTopUpMobileInfo().getMobileNumber());
			data.put("transactionDate", transaction.getConfirmationInfo().getTransactionDate());
			data.put("transactionID", transaction.getConfirmationInfo().getTransactionID());
			data.put("amount", topUpAmount);
			data.put("logoURL", topUpMobileInfo.getLogo());
			data.put("sourceOfFund", topUpMobileInfo.getEwalletSourceOfFund().getSourceType());
			data.put("totalAmount",
					calculateTotalAmount(topUpAmount, topUpMobileInfo
							.getServiceFee().calculateFee(topUpAmount),
							getEwalletSOF(topUpMobileInfo.getSourceOfFundFees())
									.calculateFee(topUpAmount)));
			
			BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);
			data.put("currentEwalletBalance", currentBalance);
			
			return this.responseFactory.createSuccessProductResonse(data);
		}

	private Object calculateTotalAmount(BigDecimal amount,
			BigDecimal serviceFee, BigDecimal sofFee) {
		return amount.add(serviceFee).add(sofFee);
	}

	private SourceOfFund getEwalletSOF(SourceOfFund[] sourceOfFundFees) {
		if (sourceOfFundFees != null) {
			for (SourceOfFund sof : sourceOfFundFees) {
				if ("EW".equals(sof.getSourceType())) {
					return sof;
				}
			}
		}

		throw new ProductAPIException("source of fund not supported.");
	}

	private void checkTopupAmount(BigDecimal amount) {

		if (!(amount.remainder(new BigDecimal(10))).equals(BigDecimal.ZERO)) {
			throw new InvalidParameterException("60000");
		} else if ((amount.compareTo(topupMinAmount)) == -1) {
			throw new InvalidParameterException("60000");
		} else if ((amount.compareTo(topupMaxAmount)) == 1) {
			throw new InvalidParameterException("60000");
		}

	}

}
