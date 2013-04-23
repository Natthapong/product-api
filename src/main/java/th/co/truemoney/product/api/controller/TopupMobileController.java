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
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.product.api.util.ValidateUtil;

@Controller
public class TopupMobileController extends BaseController {
	
	@RequestMapping(value = "/topup/mobile/draft/verifyAndCreate/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse verifyAndCreate(@PathVariable String accessToken, @RequestBody Map<String,String> request) {
		String mobileNumber = request.get("mobileNumber");
		if(!ValidateUtil.checkMobileNumber(mobileNumber)){
			throw new InvalidParameterException("40002");
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("draftTransactionID", "1111111111");
		data.put("logoURL","https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png");
		data.put("mobileNumber", "0894445266");
		data.put("amount", new BigDecimal(500));
		data.put("fee", new BigDecimal(15));
		data.put("totalAmount", new BigDecimal(515));
		
		return this.responseFactory.createSuccessProductResonse(data);
	}

}
