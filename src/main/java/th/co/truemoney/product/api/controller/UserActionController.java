package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.domain.SigninBean;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class UserActionController extends BaseController {

	@Autowired
	TmnProfileService profileService;
	
	private static final int CHANNEL_ID = 41;

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> signin(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password) {
		Map<String, Object> result = new HashMap<String, Object>();

		// validate
		validateSignin(username);

		SigninBean req = new SigninBean();
		req.setUserName(username);
		req.setPassword(password);
		req.setChannelId(CHANNEL_ID);

		try {
			String token = profileService.login(CHANNEL_ID, new Login(username, password));
			result.put("ACCESS_TOKEN", token);
			result.put(ResponseParameter.STATUS, "20000");
			result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
		} catch (ServiceInventoryException e) {
			result.put(ResponseParameter.STATUS, e.getErrorCode());
			result.put(ResponseParameter.NAMESPACE, e.getErrorNamespace());
		}
		return result;
	}

	@RequestMapping(value = "/signout/{token}", method = RequestMethod.GET)
	public Map<String, Object> signout(
			@PathVariable(value = "token") String token) {
		doSignout(token);
		Map<String, Object> result = new HashMap<String, Object>();

		result.put(ResponseParameter.STATUS, "20000");
		result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");

		return result;
	}

	public Map<String, String> extendSession(String sessionId) {
		return null;
	}

	private void validateSignin(String username) {
		if (username == null || username.isEmpty()) {
			throw new InvalidParameterException("50001");
		} else if (!ValidateUtil.checkEmail(username)) {
			throw new InvalidParameterException("50001");
		} else if (!ValidateUtil.checkMobileNumber(username)) {
			throw new InvalidParameterException("50001");
		}
	}

	@Async
	private void doSignout(String token) {
		profileService.logout(token, CHANNEL_ID);
	}

}
