package th.co.truemoney.product.api.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value = "/ewallet")
public class RegisterController extends BaseController {

	@Autowired
	TmnProfileService profileService;

	@Autowired
	UserActionController userActionController;

	@RequestMapping(value = "/profiles/validate-email", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse validateEmail(
			@RequestBody Map<String, String> request)
			throws ServiceInventoryException {

		String email = request.get("email");
		if (!ValidateUtil.checkEmail(email)) {
			throw new InvalidParameterException("40000");
		}

		String returnData = profileService.validateEmail(41, email);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email", returnData);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/profiles", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse createProfile(
			@RequestBody Map<String, String> request)
			throws ServiceInventoryException {
		String email = request.get("email");

		if (!ValidateUtil.checkEmail(email)) {
			throw new InvalidParameterException("40000");
		}

		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setEmail(email);
		tmnProfile.setFullname(request.get("fullname"));
		tmnProfile.setMobileno(request.get("mobileNumber"));
		tmnProfile.setPassword(request.get("password"));

		String returnData = profileService.createProfile(41, tmnProfile);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", returnData);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/profiles/verify-otp", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse confirmCreateProfile(
			@RequestBody Map<String, String> request)
			throws ServiceInventoryException {

		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));

		TmnProfile returnData = profileService.confirmCreateProfile(41,
				request.get("mobileNumber"), otp);
		LoginBean login = new LoginBean(returnData.getEmail(),returnData.getPassword(),"email");

		ProductResponse response = userActionController.signin(login);

		return this.responseFactory.createSuccessProductResonse(response.getData());
	}

}
