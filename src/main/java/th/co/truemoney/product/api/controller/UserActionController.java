package th.co.truemoney.product.api.controller;

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

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class UserActionController extends BaseController {

	@Autowired
	TmnProfileService profileService;

	public void setProfileService(TmnProfileService profileService) {
		this.profileService = profileService;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse signin(@RequestBody LoginBean request)
			throws ServiceInventoryException {

		// validate
		validateSignin(request.getUsername().trim(), request.getPassword().trim(),
				request.getType().trim());

		Login login = new Login(request.getUsername().trim(), request.getPassword().trim());
		String token = "";
		try {
			token = profileService.login(MOBILE_APP_CHANNEL_ID, login);
		} catch (ServiceInventoryException e) {
			String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
			if (errorcode.equals("core.1011") || errorcode.equals("core.1013")
					|| errorcode.equals("core.1014")
					|| errorcode.equals("umarket.3")
					|| errorcode.equals("umarket.4")
					|| errorcode.equals("umarket.5")) {
				if ("mobile".equals(request.getType())) {
					throw new ServiceInventoryException(400, "50001",
							"Invalid mobile or pin", "TMN-PRODUCT");
				}
			}
			throw e;
		}

		TmnProfile profile = profileService.getTruemoneyProfile(token);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("fullname", profile.getFullname());
		data.put("currentBalance", profile.getBalance());
		data.put("mobileNumber", profile.getMobileNumber());
		data.put("email", profile.getEmail());
		data.put("accessToken", token);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/signout/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody ProductResponse signout(
			@PathVariable(value = "accessToken") String token) {

		this.profileService.logout(token);

		return this.responseFactory
				.createSuccessProductResonse(new HashMap<String, Object>());
	}

	public Map<String, String> extendSession(String sessionID) {
		return null;
	}

	private void validateSignin(String username, String password, String type) {

		if (type != null) {
			if ("email".equals(type)) {
				if (!ValidateUtil.checkEmail(username)) {
					throw new InvalidParameterException("50000");
				}
				if (ValidateUtil.isEmpty(password)) {
					throw new InvalidParameterException("50000");
				}
			} else if ("mobile".equals(type)) {
				if (!ValidateUtil.checkMobileNumber(username)) {
					System.out.println("Mobile error");
					throw new InvalidParameterException("50001");
				}
				if (ValidateUtil.isEmpty(password)) {
					throw new InvalidParameterException("50001");
				}
			}
		}
	}

}
