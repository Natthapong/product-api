package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class DirectDebitController {

	@Autowired
	SourceOfFundService sourceOfFundService;

	private static final int CHANNEL_ID = 41;

	@RequestMapping(value = "/add-money/ewallet/banks", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBankList(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "token") String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			List<DirectDebit> listBank = sourceOfFundService
					.getDirectDebitSources(CHANNEL_ID, username, token);

			data.put("listOfBank", listBank);
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
}
