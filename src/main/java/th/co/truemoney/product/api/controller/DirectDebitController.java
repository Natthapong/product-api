package th.co.truemoney.product.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.TopupDirectDebitRequest;
import th.co.truemoney.product.api.domain.TopupOrderConfirmRequest;
import th.co.truemoney.product.api.domain.TopupQuotableRequest;
import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value = "/add-money/ewallet")
public class DirectDebitController extends BaseController {
	
	@Autowired
	SourceOfFundService sourceOfFundService;
	
	@RequestMapping(value = "/banks/{username}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getUserDirectDebitSources(@PathVariable String username,
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			List<DirectDebit> listBank = sourceOfFundService
					.getUserDirectDebitSources(username, accessToken);

			data.put("listOfBank", prepareData(listBank));
			result.put(ResponseParameter.STATUS, "20000");
			result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
			result.put("data", data);
		} catch (ServiceInventoryException e) {
			result.put(ResponseParameter.STATUS, e.getErrorCode());
			result.put(ResponseParameter.NAMESPACE, e.getErrorNamespace());
		}

		return messageManager.mapStatusMessage(result);
	}
	
	/**
	 * Create direct debit quotation 
	 * Verify amount
	 */
	@RequestMapping(value = "/directdebit/quote/create/{accessToken}", method = RequestMethod.POST)
	public 
	@ResponseBody Map<String, Object> createDirectDebitTopupQuote(
			@RequestBody TopupDirectDebitRequest request, 
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		return messageManager.mapStatusMessage(result);
	}
	
	/**
	 * Get direct debit quotation details
	 */
	@RequestMapping(value = "/directdebit/quote/details/{accessToken}", method = RequestMethod.GET)
	public 
	@ResponseBody Map<String, Object> getDirectDebitTopupQuoteDetials(
			@RequestBody TopupQuotableRequest request, 
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		return messageManager.mapStatusMessage(result);
	}
	
	/**
	 * Send OTP
	 * service-inventory-api : requestPlaceOrder(String quoteId, String accessToken)
	 * return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/create/{accessToken}", method = RequestMethod.POST)
	public 
	@ResponseBody Map<String, Object> placeDirectDebitTopupOrder(
			@RequestBody TopupQuotableRequest request, 
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		return messageManager.mapStatusMessage(result);
	}
	
	/**
	 * Confirm transaction
	 * confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken
	 * return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/confirm/{accessToken}", method = RequestMethod.PUT)
	public 
	@ResponseBody Map<String, Object> confirmDirectDebitTopuOrder(
			@RequestBody TopupOrderConfirmRequest request, 
			@PathVariable String accessToken) {
		return null;
	}
	
	/**
	 * Polling for transaction status
	 */
	@RequestMapping(value = "/directdebit/order/{topUpOrderId}/status/{accessToken}", method = RequestMethod.GET)
	public 
	@ResponseBody Map<String, Object> getDirectDebitTopupStatus(
			@PathVariable String topUpOrderId, 
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		return messageManager.mapStatusMessage(result);
	}
	
	/**
	 * Get transaction detail after transaction done successfully
	 */
	@RequestMapping(value = "/directdebit/order/{topUpOrderId}/details/{accessToken}", method = RequestMethod.GET)
	public Map<String, Object> getDirectDebitTopupDetails(
			@PathVariable String topUpOrderId, 
			@PathVariable String accessToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		return messageManager.mapStatusMessage(result);
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
