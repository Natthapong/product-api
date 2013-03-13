package th.co.truemoney.product.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.util.MessageManager;

@Controller
public class UserActionController {
	
	@Autowired
	private MessageManager messageManager;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> login(
			@RequestParam(value="username", required=false) String username, 
			@RequestParam(value="accessKey", required=false) String accessKey) {
		Map<String, Object> result = new HashMap<String, Object>();
		// validate
		if (username == null || username.isEmpty()) {
			result.put("status", "1");
			//result.put("descriptionEn", "Invalid username");
			return result;
		}
		
		result.put("status", "20000");
		//result.put("descriptionEn", messageManager.getMessageEn("TMN-PRODUCT", "20000"));
		//result.put("descriptionTh", messageManager.getMessageTh("TMN-PRODUCT", "20000"));
		return result;
	}
	
	public Map<String, String> logout(String sessessionId) {
		return null;
	}
	
	public Map<String, String> extendSession(String sessionId) {
		return null;
	}
	
}
