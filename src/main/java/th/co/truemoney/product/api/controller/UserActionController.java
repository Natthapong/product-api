package th.co.truemoney.product.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.ResponseParameter;

@Controller
public class UserActionController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> login(
			@RequestParam(value="username", required=false) String username, 
			@RequestParam(value="accessKey", required=false) String accessKey) {
		Map<String, Object> result = new HashMap<String, Object>();
		// validate
		if (username == null || username.isEmpty()) {
			result.put(ResponseParameter.STATUS, "1");
			return result;
		}
		
		result.put(ResponseParameter.STATUS, "20000");
		result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		return result;
	}
	
	public Map<String, String> logout(String sessessionId) {
		return null;
	}
	
	public Map<String, String> extendSession(String sessionId) {
		return null;
	}
	
}
