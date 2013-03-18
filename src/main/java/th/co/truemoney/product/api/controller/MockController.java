package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.util.ResponseParameter;

@Controller
public class MockController {

	@RequestMapping(value = "/mock/add-money/ewallet/banks/{username}/{accessToken}", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, Object> getDirectDebitInformation(
			@PathVariable(value = "username") String username,
			@PathVariable(value = "accessToken") String accessToken) {

		JSONObject response = new JSONObject();
		JSONObject result = new JSONObject();

		List<Map<String, Object>> listOfBank = new ArrayList<Map<String, Object>>();
		Map<String, Object> scb = new HashMap<String,Object>();
		scb.put("sourceOfFundID", "1");
		scb.put("bankCode", "SCB");
		scb.put("bankName", "ไทยพาณิชย์");
		scb.put("bankNumber", "XXXX7890");
		scb.put("min", "30");
		scb.put("max", "3000");
		scb.put("urlLogo",
				"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-SCB-2.png");
		listOfBank.add(scb);

		Map<String, Object> bbl = new HashMap<String,Object>();
		bbl.put("sourceOfFundID", "2");
		bbl.put("bankCode", "BBL");
		bbl.put("bankName", "กรุงเทพ");
		bbl.put("bankNumber", "XXXX7891");
		bbl.put("min", "30");
		bbl.put("max", "30000");
		bbl.put("urlLogo",
				"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-bk-2.png");
		listOfBank.add(bbl);

		Map<String, Object> ktb = new HashMap<String,Object>();
		ktb.put("sourceOfFundID", "3");
		ktb.put("bankCode", "KTB");
		ktb.put("bankName", "กรุงไทย");
		ktb.put("bankNumber", "XXXX7892");
		ktb.put("min", "30");
		ktb.put("max", "30000");
		ktb.put("urlLogo",
				"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ktb-2.png");
		listOfBank.add(ktb);

		Map<String, Object> bay = new HashMap<String,Object>();
		bay.put("sourceOfFundID", "4");
		bay.put("bankCode", "BAY");
		bay.put("bankName", "กรุงศรีอยุธยา");
		bay.put("bankNumber", "XXXX7893");
		bay.put("min", "30");
		bay.put("max", "3000");
		bay.put("urlLogo",
				"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ks-2.png");
		listOfBank.add(bay);

		result.put("listOfBank", listOfBank);

		response.put(ResponseParameter.STATUS, "20000");
		response.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		response.put("data", result);
		return response;

	}

	@RequestMapping(value = "/mock/add-money/verify/ewallet/{amount}/{bankCode}/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> verifyEwallet(
			@PathVariable(value = "amount") BigDecimal amount,
			@PathVariable(value = "bankCode") String bankCode,
			@PathVariable(value = "accessToken") String token) {
		Map<String, Object> data = new HashMap<String,Object>();
		if ("SCB".equals(bankCode)) {
			data.put("urlLogo",
					"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-SCB-2.png");
			data.put("bankNameTH", "ไทยพาณิชย์");
			data.put("bankNameEN", "scb");
		} else if ("KTB".equals(bankCode)) {
			data.put("urlLogo",
					"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ktb-2.png");
			data.put("bankNameTH", "กรุงไทย");
			data.put("bankNameEN", "ktb");
		} else if ("BBL".equals(bankCode)) {
			data.put("urlLogo",
					"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-bk-2.png");
			data.put("bankNameTH", "กรุงเทพ");
			data.put("bankNameEN", "bbl");
		} else if ("BAY".equals(bankCode)) {
			data.put("urlLogo",
					"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-ks-2.png");
			data.put("bankNameTH", "กรุงศรีอยุธยา");
			data.put("bankNameEN", "bay");
		}

		data.put("bankCode", bankCode);
		data.put("bankNumber", "XXXX7890");
		data.put("fee", 1.00);
		data.put("amount", amount);
		data.put("transactionID", "1234567890");

		JSONObject response = new JSONObject();

		response.put(ResponseParameter.STATUS, "20000");
		response.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		response.put("data", data);
		return response;

	}

	@RequestMapping(value = "/mock/add-money/send/otp/{transactionID}/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> sendOTP(
			@PathVariable(value = "transactionID") String transactionID,
			@PathVariable(value = "accessToken") String token) {
		Map<String, Object> data = new HashMap<String,Object>();

		data.put("otp", "287492");
		data.put("refCode", "VNBM");
		data.put("amount", 1000.00);

		JSONObject response = new JSONObject();

		response.put(ResponseParameter.STATUS, "20000");
		response.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		response.put("data", data);
		return response;

	}

	@RequestMapping(value = "/mock/add-money/confirm/ewallet/{accessToken}", method = RequestMethod.PUT)
	public @ResponseBody
	Map<String, Object> confirmEwallet(
			@PathVariable(value = "accessToken") String token) {
		JSONObject response = new JSONObject();
		Map<String, Object> data = new HashMap<String, Object>();
		response.put(ResponseParameter.STATUS, "20000");
		response.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		response.put("data", data);
		return response;

	}

	@RequestMapping(value = "/mock/add-money/checkstatus/ewallet/{accessToken}", method = RequestMethod.PUT)
	public @ResponseBody
	Map<String, Object> checkStatus(
			@PathVariable(value = "accessToken") String token) {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("urlLogo",
				"https://secure.truemoney-dev.com/m/tmn_webview/images/normal/Bank-SCB-2.png");
		data.put("bankNameTH", "ไทยพาณิชย์");
		data.put("bankNameEN", "scb");
		data.put("bankCode", "scb");
		data.put("bankNumber", "XXXX9845");
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMM/dd HH:mm");
		String dateNow = formatter.format(currentDate.getTime());
		data.put("timestamp", dateNow);
		data.put("transactionID", "12323423423");
		data.put("amount", 1000.00);
		data.put("fee", 3.00);
		data.put("currentBalance", 10000.00);

		JSONObject response = new JSONObject();

		response.put(ResponseParameter.STATUS, "20000");
		response.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		response.put("data", data);
		return response;

	}

}
