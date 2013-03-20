package th.co.truemoney.product.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.domain.TopupDirectDebitRequest;
import th.co.truemoney.product.api.domain.TopupOrderConfirmRequest;
import th.co.truemoney.product.api.domain.TopupQuotableRequest;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

@Controller
@RequestMapping(value = "/add-money/ewallet")
public class DirectDebitController extends BaseController {
	
	@Autowired
	SourceOfFundService sourceOfFundService;
	
	@Autowired
	TopUpService topupService;
	
	@RequestMapping(value = "/banks/{username}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getUserDirectDebitSources(
			@PathVariable String username,
			@PathVariable String accessToken,
			HttpServletResponse response) {
		
		List<DirectDebit> listBank = sourceOfFundService.getUserDirectDebitSources(username, accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("listOfBank", prepareData(listBank));
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	/**
	 * Create direct debit quotation 
	 * Verify amount
	 */
	@RequestMapping(value = "/directdebit/quote/create/{accessToken}", method = RequestMethod.POST)
	public 
	@ResponseBody ProductResponse createDirectDebitTopupQuote(
			@RequestBody TopupDirectDebitRequest request, 
			@PathVariable String accessToken,
			HttpServletResponse response) {
		
		QuoteRequest quoteRequest = new QuoteRequest();
		quoteRequest.setAmount(request.getAmount());
		quoteRequest.setChecksum(request.getChecksum());
		
		TopUpQuote quote = this.topupService.createTopUpQuoteFromDirectDebit(
				request.getSourceOfFundID(), quoteRequest, accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("quoteID", quote.getID());
		data.put("amount", quote.getAmount());
		data.put("fee", quote.getTopUpFee());
		data.put("bankNumber", "");//TODO
		data.put("bankNameEN", "");//TODO
		data.put("bankNameTH", "");//TODO
		data.put("sourceOfFundID", quote.getSourceOfFund().getSourceOfFundID());//TODO
		data.put("accessToken", quote.getAccessTokenID());//TODO
		data.put("urlLogo", "");//TODO
		
		return responseFactory.createSuccessProductResonse(data);
	}
	
	/**
	 * Get direct debit quotation details
	 */
	@RequestMapping(value = "/directdebit/quote/details/{accessToken}", method = RequestMethod.GET)
	public 
	@ResponseBody ProductResponse getDirectDebitTopupQuoteDetials(
			@RequestBody TopupQuotableRequest request, 
			@PathVariable String accessToken) {
		//TODO
		return null;
	}
	
	/**
	 * Send OTP
	 * service-inventory-api : requestPlaceOrder(String quoteId, String accessToken)
	 * return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/create/{accessToken}", method = RequestMethod.POST)
	public 
	@ResponseBody ProductResponse placeDirectDebitTopupOrder(
			@RequestBody TopupQuotableRequest request, 
			@PathVariable String accessToken) {
		
		TopUpOrder order = this.topupService.requestPlaceOrder(request.getQuoteID(), accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topupOrderID", order.getConfirmationInfo().getTransactionID());
		data.put("amount", order.getAmount());
		data.put("otpRefCode", order.getOtpReferenceCode());
		
		return responseFactory.createSuccessProductResonse(data);
	}
	
	/**
	 * Confirm transaction
	 * confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken
	 * return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/confirm/{accessToken}", method = RequestMethod.PUT)
	public 
	@ResponseBody ProductResponse confirmDirectDebitTopuOrder(
			@RequestBody TopupOrderConfirmRequest request, 
			@PathVariable String accessToken) {
		return null;
	}
	
	/**
	 * Polling for transaction status
	 */
	@RequestMapping(value = "/directdebit/order/{topupOrderID}/status/{accessToken}", method = RequestMethod.GET)
	public 
	@ResponseBody ProductResponse getDirectDebitTopupStatus(
			@PathVariable String topupOrderID, 
			@PathVariable String accessToken) {
		return null;
	}
	
	/**
	 * Get transaction detail after transaction done successfully
	 */
	@RequestMapping(value = "/directdebit/order/{topupOrderID}/details/{accessToken}", method = RequestMethod.GET)
	public ProductResponse getDirectDebitTopupDetails(
			@PathVariable String topupOrderID, 
			@PathVariable String accessToken) {
		return null;
	}

	private List<JSONObject> prepareData(List<DirectDebit> listBank) {
		List<JSONObject> realData = new ArrayList<JSONObject>();

		for (DirectDebit debit : listBank) {
			JSONObject returnData = new JSONObject();
			returnData.put("bankCode", debit.getBankCode());
			returnData.put("bankNumber", debit.getBankAccountNumber());
			returnData.put("bankNameTH", debit.getBankNameTh());
			returnData.put("bankNameEN", debit.getBankNameEn());
			returnData.put("minAmount", debit.getMinAmount());
			returnData.put("maxAmount", debit.getMaxAmount());
			returnData.put("sourceOfFundID", debit.getSourceOfFundID());

			if (debit.getBankCode().equals("SCB")) {
				returnData
						.put("urlLogo",
								"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-SCB-2.png");
			} else if (debit.getBankCode().equals("KTB")) {
				returnData
						.put("urlLogo",
								"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ktb-2.png");
			} else if (debit.getBankCode().equals("BBL")) {
				returnData
						.put("urlLogo",
								"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-bk-2.png");
			} else if (debit.getBankCode().equals("BAY")) {
				returnData
						.put("urlLogo",
								"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ks-2.png");
			}
			realData.add(returnData);
		}
		return realData;
	}

}
