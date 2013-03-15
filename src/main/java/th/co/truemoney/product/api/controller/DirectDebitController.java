package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RequestMapping(value = "/add-money/ewallet")
public class DirectDebitController {

	@Autowired
	SourceOfFundService sourceOfFundService;

	private static final int CHANNEL_ID = 41;

	@RequestMapping(value = "/banks", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBankList(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "token") String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			List<DirectDebit> listBank = sourceOfFundService
					.getDirectDebitSources(CHANNEL_ID, username, token);

			data.put("listOfBank", prepareData(listBank));
			result.put(ResponseParameter.STATUS, "20000");
			result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
			result.put("data", data);
		} catch (ServiceInventoryException e) {
			result.put(ResponseParameter.STATUS, e.getErrorCode());
			result.put(ResponseParameter.NAMESPACE, e.getErrorNamespace());
		}

		return result;
	}

	public Map<String, String> verify(BigDecimal amount) {
		return null;
	}

	public Map<String, String> confirm(BigDecimal amount) {
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
			returnData.put("sourceOfFundID", debit.getSourceOfFundId());

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
