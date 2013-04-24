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
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
public class TopupMobileController extends BaseController {
	
	@Autowired
	private TopUpMobileService topUpMobileService;
	
	private static final int topupMinAmount = 10;
	private static final int topupMaxAmount = 1000;
	
	@RequestMapping(value = "/topup/mobile/draft/verifyAndCreate/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse verifyAndCreate(@PathVariable String accessToken, @RequestBody Map<String,String> request) {
		String mobileNumber = request.get("mobileNumber");
		
		String amount = request.get("amount");
		
		if(!ValidateUtil.checkMobileNumber(mobileNumber)){
			throw new InvalidParameterException("40002");
		}
		
		if(ValidateUtil.isEmpty(amount)){
			throw new InvalidParameterException("60000");
		}
		
		checkTopupAmount(Integer.parseInt(amount));
		
		TopUpMobileDraft draft = topUpMobileService.verifyAndCreateTopUpMobileDraft(mobileNumber, new BigDecimal(amount), accessToken);
		TopUpMobile topUpMobileInfo = draft.getTopUpMobileInfo();
		BigDecimal topUpAmount = topUpMobileInfo.getAmount();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("draftTransactionID", draft.getID());
		
		data.put("logoURL", topUpMobileInfo.getLogo());
		data.put("mobileNumber", String.valueOf(topUpMobileInfo.getMobileNumber()).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2-$3"));
		data.put("amount", topUpMobileInfo.getAmount());
		data.put("fee", topUpMobileInfo.getServiceFee().getFeeRate());
		data.put("totalAmount", calculateTotalAmount(topUpAmount, topUpMobileInfo.getServiceFee().calculateFee(topUpAmount), getEwalletSOF(topUpMobileInfo.getSourceOfFundFees()).calculateFee(topUpAmount)));
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/topup/mobile/sendotp/{draftTransactionID}/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse sendOTP(@PathVariable String accessToken,@PathVariable String draftTransactionID) {
		OTP otp = topUpMobileService.sendOTP(draftTransactionID, accessToken);
		
		TopUpMobileDraft draft = topUpMobileService.getTopUpMobileDraftDetail(draftTransactionID, accessToken);
		TopUpMobile topUpMobileInfo = draft.getTopUpMobileInfo();
		BigDecimal topUpAmount = topUpMobileInfo.getAmount();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refCode", otp.getReferenceCode());
		data.put("totalAmount", calculateTotalAmount(topUpAmount, topUpMobileInfo.getServiceFee().calculateFee(topUpAmount), getEwalletSOF(topUpMobileInfo.getSourceOfFundFees()).calculateFee(topUpAmount)));
		
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
	
	private void checkTopupAmount(int amount){
		
		if((amount%10) != 0){
			throw new InvalidParameterException("60000");
		}else if(amount < topupMinAmount){
			throw new InvalidParameterException("60000");
		}else if(amount > topupMaxAmount){
			throw new InvalidParameterException("60000");
		}

	}

}
