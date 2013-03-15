package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.product.api.util.ResponseParameter;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class UserActionController extends BaseController {

	@Autowired
	TmnProfileService profileService;

	private static final int CHANNEL_ID = 41;

	public void setProfileService(TmnProfileService profileService) {
		this.profileService = profileService;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> signin(@RequestBody LoginBean request) throws ServiceInventoryException {
		Map<String, Object> result = new HashMap<String, Object>();
		// validate
		validateSignin(request.getUsername());
		try {
			Login login = new Login(request.getUsername(), request.getPassword());
			String token = profileService.login(CHANNEL_ID, login);

			TmnProfile profile = getUserProfile(token, request.getPassword());

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("accessToken", token);
			data.put("fullname", profile.getFullname());
			data.put("currentBalance", profile.getBalance());
			result.put(ResponseParameter.STATUS, "20000");
			result.put(ResponseParameter.NAMESPACE, "TMN-PRODUCT");
			result.put("data", data);
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

	public Map<String, String> extendSession(String sessionID) {
		return null;
	}

	private TmnProfile getUserProfile(String accesstoken,
			String checksum) {
		return profileService.getTruemoneyProfile(CHANNEL_ID, accesstoken,
				checksum);
	}

	private void validateSignin(String username) {

		if (!ValidateUtil.checkEmail(username)) {
			if (!ValidateUtil.checkMobileNumber(username)) {
				throw new InvalidParameterException("50001");
			}
		}
	}

	@Async
	private void doSignout(String token) {
		profileService.logout(CHANNEL_ID, token);
	}

}
