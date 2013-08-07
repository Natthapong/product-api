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
import th.co.truemoney.serviceinventory.ewallet.domain.ChangePin;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class UserActionController extends BaseController {

	private static final Integer MOBILE_CHANNEL_ID = 40;

	@Autowired
	private TmnProfileService profileService;

	@Autowired
	private ClientCredential appLogin;


	public void setProfileService(TmnProfileService profileService) {
		this.profileService = profileService;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse signin(@RequestBody LoginBean request)
			throws ServiceInventoryException {

		// validate
		validateSignin(request.getUsername().toLowerCase().trim(), request.getPassword().trim(),
				request.getType().trim());

		EWalletOwnerCredential userLogin = new EWalletOwnerCredential(
				request.getUsername().trim(),
				request.getPassword().trim(),
				MOBILE_CHANNEL_ID);

		String token = "";
		try {
			token = profileService.login(userLogin, appLogin);
		} catch (ServiceInventoryException e) {
			String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
			if (errorcode.equals("core.1011") || errorcode.equals("core.1013")
					|| errorcode.equals("core.1014")
					|| errorcode.equals("core.3")
					|| errorcode.equals("core.4")
					|| errorcode.equals("core.5")) {
				if ("mobile".equals(request.getType())) {
					throw new ServiceInventoryException(400, "50001",
							"Invalid mobile or pin", "TMN-PRODUCT");
				}
			}
			throw e;
		}

		ProductResponse response = getUserProfile(token);
		response.getData().put("accessToken", token);
		return response;
	}

	@RequestMapping(value = "/signout/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody ProductResponse signout(
			@PathVariable(value = "accessToken") String token) {

		this.profileService.logout(token);

		return this.responseFactory
				.createSuccessProductResonse(new HashMap<String, Object>());
	}

	@RequestMapping(value = "/profile/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getUserProfile(@PathVariable String accessToken) {
		TmnProfile tmnProfile = profileService.getTruemoneyProfile(accessToken);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("email", tmnProfile.getEmail());
		data.put("fullname", tmnProfile.getFullname());
		data.put("mobileNumber", tmnProfile.getMobileNumber());
		data.put("currentBalance", tmnProfile.getBalance().toString());
		data.put("hasPassword", tmnProfile.getHasPassword());
		data.put("hasPin", tmnProfile.getHasPin());
		data.put("imageURL", tmnProfile.getImageURL());
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/profile/change-pin/{accessToken}", method = RequestMethod.PUT)
	public @ResponseBody ProductResponse changePin(
			@PathVariable(value = "accessToken") String accessTokenID,
			@RequestBody Map<String, String> request) {
		
		ChangePin changePin = new ChangePin();
		changePin.setOldPin(request.get("oldPin"));
		changePin.setPin(request.get("pin"));
		
		String mobileNumber = profileService.changePin(accessTokenID, changePin);
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("mobileNumber", mobileNumber);

		return this.responseFactory.createSuccessProductResonse(new HashMap<String, Object>());
	}
	
	@RequestMapping(value = "/profile/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse updateProfile(@PathVariable(value = "accessToken") String accessTokenID,
			@RequestBody Map<String, String> request) {
		
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname(request.get("fullname"));	
		
		tmnProfile = profileService.updateTruemoneyProfile(accessTokenID, tmnProfile);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("email", tmnProfile.getEmail());
		data.put("fullname", tmnProfile.getFullname());
		data.put("mobileNumber", tmnProfile.getMobileNumber());
		data.put("currentBalance", tmnProfile.getBalance().toString());
		data.put("hasPassword", tmnProfile.getHasPassword());
		data.put("hasPin", tmnProfile.getHasPin());
		data.put("imageURL", tmnProfile.getImageURL());
		
		return this.responseFactory.createSuccessProductResonse(data);
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
