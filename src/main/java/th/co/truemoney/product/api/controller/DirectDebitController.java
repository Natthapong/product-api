package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
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
import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

@Controller
public class DirectDebitController extends BaseController {

	@Autowired
	DirectDebitSourceOfFundService sourceOfFundService;

	@Autowired
	TopUpService topupService;

	@Autowired
	TmnProfileService profileService;

	private static final String imageBankURL = "https://secure.truemoney-dev.com/m/tmn_webview/";

	@RequestMapping(value = "/add-money/ewallet/banks/{username}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getUserDirectDebitSources(
			@PathVariable String username, @PathVariable String accessToken,
			HttpServletResponse response) {

		List<DirectDebit> listBank = sourceOfFundService
				.getUserDirectDebitSources(username, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("listOfBank", prepareData(listBank));

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Create direct debit quotation Verify amount
	 */
	@RequestMapping(value = "/directdebit/quote/create/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse createDirectDebitTopupQuote(
			@RequestBody TopupDirectDebitRequest request,
			@PathVariable String accessToken, HttpServletResponse response) {

		TopUpQuote quote = this.topupService.createTopUpQuoteFromDirectDebit(
				request.getSourceOfFundID(), request.getAmount(), accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("quoteID", quote.getID());
		data.put("amount", quote.getAmount());
		data.put("fee", quote.getTopUpFee());
		DirectDebit db = (DirectDebit) quote.getSourceOfFund();
		data.put("bankNumber", db.getBankAccountNumber());
		data.put("bankNameEn", db.getBankNameEn());
		data.put("bankNameTh", db.getBankNameTh());
		data.put("urlLogo", getUrlLogo(db.getBankCode()));
		data.put("sourceOfFundID", quote.getSourceOfFund().getSourceOfFundID());
		data.put("accessToken", quote.getAccessTokenID());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Get direct debit quotation details
	 */
	@RequestMapping(value = "/directdebit/quote/details/{accessToken}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getDirectDebitTopupQuoteDetials(
			@RequestBody TopupQuotableRequest request,
			@PathVariable String accessToken) {

		TopUpQuote quote = this.topupService.getTopUpQuoteDetails(
				request.getQuoteID(), accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("quoteID", quote.getID());
		data.put("amount", quote.getAmount());
		data.put("fee", quote.getTopUpFee());
		DirectDebit db = (DirectDebit) quote.getSourceOfFund();
		data.put("bankNumber", db.getBankAccountNumber());
		data.put("bankNameEn", db.getBankNameEn());
		data.put("bankNameTh", db.getBankNameTh());
		data.put("urlLogo", getUrlLogo(db.getBankCode()));
		data.put("sourceOfFundID", quote.getSourceOfFund().getSourceOfFundID());
		data.put("accessToken", quote.getAccessTokenID());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Send OTP service-inventory-api : requestPlaceOrder(String quoteId, String
	 * accessToken) return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/create/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse placeDirectDebitTopupOrder(
			@RequestBody TopupQuotableRequest request,
			@PathVariable String accessToken) {

		OTP otp = this.topupService.sendOTPConfirm(request.getQuoteID(),
				accessToken);
		TopUpQuote quote = this.topupService.getTopUpQuoteDetails(
				request.getQuoteID(), accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("quoteID", quote.getID());
		data.put("amount", quote.getAmount());
		data.put("fee", quote.getTopUpFee());
		data.put("otpRefCode", otp.getReferenceCode());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Confirm transaction confirmPlaceOrder(String quoteID, OTP otp, String
	 * accessToken return TopUpOrder;
	 */
	@RequestMapping(value = "/directdebit/order/confirm/{accessToken}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmDirectDebitTopuOrder(
			@RequestBody TopupOrderConfirmRequest request,
			@PathVariable String accessToken) {
		OTP otp = new OTP();
		otp.setReferenceCode(request.getOtpRefCode());
		otp.setOtpString(request.getOtpString());

		DraftTransaction.Status quoteStatus = this.topupService.confirmOTP(
				request.getTopupOrderID(), otp, accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topupStatus", quoteStatus.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Polling for transaction status
	 */
	@RequestMapping(value = "/directdebit/order/{quoteID}/status/{accessToken}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getDirectDebitTopupStatus(@PathVariable String quoteID,
			@PathVariable String accessToken) {

		Transaction.Status status = this.topupService.getTopUpProcessingStatus(
				quoteID, accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("topupStatus", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	/**
	 * Get transaction detail after transaction done successfully
	 */
	@RequestMapping(value = "/directdebit/order/{quoteID}/details/{accessToken}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getDirectDebitTopupDetails(@PathVariable String quoteID,
			@PathVariable String accessToken) {

		TopUpOrder order = this.topupService.getTopUpOrderResults(quoteID,
				accessToken);
		BigDecimal balance = this.profileService.getEwalletBalance(accessToken);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transactionID", order.getConfirmationInfo()
				.getTransactionID());
		data.put("transactionDate", order.getConfirmationInfo()
				.getTransactionDate());

		TopUpQuote quote = order.getQuote();
		data.put("amount", quote.getAmount());
		DirectDebit db = (DirectDebit) quote.getSourceOfFund();
		data.put("bankNumber", db.getBankAccountNumber());
		data.put("bankNameEn", db.getBankNameEn());
		data.put("bankNameTh", db.getBankNameTh());
		data.put("urlLogo", getUrlLogo(db.getBankCode()));
		data.put("fee", quote.getTopUpFee());
		data.put("currentBalance", balance);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	private List<JSONObject> prepareData(List<DirectDebit> listBank) {
		List<JSONObject> realData = new ArrayList<JSONObject>();

		for (DirectDebit debit : listBank) {
			JSONObject returnData = new JSONObject();
			returnData.put("bankCode", debit.getBankCode());
			returnData.put("bankNumber", debit.getBankAccountNumber());
			returnData.put("bankNameTh", debit.getBankNameTh());
			returnData.put("bankNameEn", debit.getBankNameEn());
			returnData.put("minAmount", debit.getMinAmount());
			returnData.put("maxAmount", debit.getMaxAmount());
			returnData.put("sourceOfFundID", debit.getSourceOfFundID());
			returnData.put("urlLogo", getUrlLogo(debit.getBankCode()));
			realData.add(returnData);
		}
		return realData;
	}

	private String getUrlLogo(String bankCode) {
		String returnData = new String();
		if (bankCode.equals("SCB")) {
			returnData = imageBankURL + "images/logo_bank/scb@2x.png";
		} else if (bankCode.equals("KTB")) {
			returnData = imageBankURL + "images/logo_bank/ktb@2x.png";
		} else if (bankCode.equals("BBL")) {
			returnData = imageBankURL + "images/logo_bank/bbl@2x.png";
		} else if (bankCode.equals("BAY")) {
			returnData = imageBankURL + "images/logo_bank/bay@2x.png";
		} else if (bankCode.equals("KBANK")) {
			returnData = imageBankURL + "images/logo_bank/kbank@2x.png";
		} else if (bankCode.equals("TMB")) {
			returnData = imageBankURL + "images/logo_bank/tmb@2x.png";
		}

		return returnData;
	}

}
