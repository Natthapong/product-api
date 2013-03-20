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

	private static final int CHANNEL_ID = 41;

	public void setProfileService(TmnProfileService profileService) {
		this.profileService = profileService;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse signin(@RequestBody LoginBean request) throws ServiceInventoryException {

		// validate
		validateSignin(request.getUsername(), request.getPassword());
		
		Login login = new Login(request.getUsername(), request.getPassword());
		String token = profileService.login(CHANNEL_ID, login);

		//TmnProfile profile = getUserProfile(token, request.getPassword());
		//Map<String, Object> data = new HashMap<String, Object>();
		//data.put("fullname", profile.getFullname());
		//data.put("currentBalance", profile.getBalance());
		
		Map<String, Object> mockData = new HashMap<String, Object>();
		mockData.put("accessToken", token);
		mockData.put("fullname", "John Doe");
		mockData.put("currentBalance", 0.00);
		
		return this.responseFactory.createSuccessProductResonse(mockData);
	}

	@RequestMapping(value = "/signout/{accessToken}", method = RequestMethod.POST)
	public ProductResponse signout(@PathVariable(value = "accessToken") String token) {
		
		doSignout(token);
		
		return this.responseFactory.createSuccessProductResonse(new HashMap<String, Object>());
	}

	public Map<String, String> extendSession(String sessionID) {
		return null;
	}

	private TmnProfile getUserProfile(String accesstoken, String checksum) {
		return profileService.getTruemoneyProfile(accesstoken);
	}

	private void validateSignin(String username, String password) {

		if (!ValidateUtil.checkEmail(username)) {
			if (!ValidateUtil.checkMobileNumber(username)) {
				throw new InvalidParameterException("50001");
			}
		}
		
		if(ValidateUtil.isEmpty(password)){
			throw new InvalidParameterException("50001");
		}
	}

	@Async
	private void doSignout(String token) {
		profileService.logout(token);
	}

}
