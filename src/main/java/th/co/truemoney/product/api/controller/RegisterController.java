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
		String returnData = "";
		try {
			returnData = profileService.validateEmail(MOBILE_APP_CHANNEL_ID,
					email);
		} catch (ServiceInventoryException e) {
			String errorcode = String.format("%s.%s", e.getErrorNamespace(),
					e.getErrorCode());
			if (errorcode.equals("next.18")) {
				throw new ServiceInventoryException("10000",
						"Email is already in used.", "TMN-PRODUCT");
			}
			throw e;
		}

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
		if (!ValidateUtil.checkMobileNumber(request.get("mobileNumber"))) {
			throw new InvalidParameterException("40001");
		}

		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setEmail(email);
		tmnProfile.setFullname(request.get("fullname"));
		tmnProfile.setMobileNumber(request.get("mobileNumber"));
		tmnProfile.setPassword(request.get("password"));
		tmnProfile.setThaiID(request.get("thaiID"));

		OTP returnData = new OTP();

		try {
			returnData = profileService.createProfile(MOBILE_APP_CHANNEL_ID,
					tmnProfile);
		} catch (ServiceInventoryException e) {
			String errorcode = String.format("%s.%s", e.getErrorNamespace(),
					e.getErrorCode());
			if (errorcode.equals("next.18")) {
				throw new ServiceInventoryException("10001",
						"Mobile number is already in used.", "TMN-PRODUCT");
			}
			throw e;
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", returnData.getReferenceCode());
		data.put("mobileNumber", returnData.getMobileNumber());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/profiles/verify-otp", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse confirmCreateProfile(
			@RequestBody Map<String, String> request)
			throws ServiceInventoryException {

		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));

		try {
			profileService.confirmCreateProfile(MOBILE_APP_CHANNEL_ID, otp);
		} catch (ServiceInventoryException e) {
			String errorcode = String.format("%s.%s", e.getErrorNamespace(),
					e.getErrorCode());
			if (!errorcode.equals("next.18")) {
				throw e;
			}

		}
		Map<String, Object> data = new HashMap<String, Object>();

		return this.responseFactory.createSuccessProductResonse(data);
	}

}
