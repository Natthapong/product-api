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
import th.co.truemoney.product.api.manager.ProfileImageManager;
import th.co.truemoney.product.api.manager.SecurityManager;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
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

	@Autowired
	private ProfileImageManager profileImageManager;
	
    @Autowired
    private SecurityManager securityManager;


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
			if ("mobile".equals(request.getType()) && (errorcode.equals("core.1011") 
					|| errorcode.equals("core.1013")
					|| errorcode.equals("core.1014")
					|| errorcode.equals("core.3")
					|| errorcode.equals("core.4")
					|| errorcode.equals("core.5"))) {
					throw new ServiceInventoryException(400, "50001","Invalid mobile or pin", "TMN-PRODUCT");
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
	public ProductResponse getUserProfile(@PathVariable String accessToken) throws ServiceInventoryException {
		try {
			TmnProfile tmnProfile = profileService.getTruemoneyProfile(accessToken);
			String profileImageURL = profileImageManager.generateProfileImageURL(accessToken, tmnProfile.getImageFileName());
	
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("email", tmnProfile.getEmail());
			data.put("fullname", tmnProfile.getFullname());
			data.put("mobileNumber", tmnProfile.getMobileNumber());
			data.put("currentBalance", tmnProfile.getBalance().toString());
			data.put("hasPassword", tmnProfile.getHasPassword());
			data.put("hasPin", tmnProfile.getHasPin());
			data.put("imageURL", profileImageURL);
			
			return this.responseFactory.createSuccessProductResonse(data);
		} catch (ServiceInventoryException e){
			String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
			if(errorcode != null && errorcode.equals("core.3")) {
				throw new ServiceInventoryException(400, "020003",
						"Agent not found", "TMN-PRODUCT");
			}
			throw e;
		}
	}
	
	@RequestMapping(value = "/profile/change-password/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody

	public ProductResponse changePassword(@PathVariable String accessToken, @RequestBody Map<String, String> request) 
			throws ServiceInventoryException {
		
		try {
			String oldPassword = request.get("oldPassword");
			String newPassword = request.get("password");
			String email = profileService.changePassword(accessToken, oldPassword, securityManager.encryptRSA(newPassword));
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("email", email);
			
			return this.responseFactory.createSuccessProductResonse(data);
		} catch (ServiceInventoryException e){
			String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
			if(errorcode != null) {
				if(errorcode.equals("core.1011")) {
					throw new ServiceInventoryException(400, "011011", "Bad token", "TMN-PRODUCT");
				} else if(errorcode.equals("core.1014")) {
					throw new ServiceInventoryException(400, "011014", "Bad password", "TMN-PRODUCT");
				} else if(errorcode.equals("core.2010")) {
					throw new ServiceInventoryException(400, "012010", "Pin access denied", "TMN-PRODUCT");
				}
			}
			throw e;
		}
	}

	@RequestMapping(value = "/profile/change-pin/{accessToken}", method = RequestMethod.PUT)
	public @ResponseBody ProductResponse changePin(
			@PathVariable(value = "accessToken") String accessTokenID,
			@RequestBody Map<String, String> request) throws ServiceInventoryException {
		try {
			String oldPin = request.get("oldPin");
			String newPin = request.get("pin");
			
			String mobileNumber = profileService.changePin(accessTokenID, oldPin,  securityManager.encryptRSA(newPin));
			
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("mobileNumber", mobileNumber);
	
			return this.responseFactory.createSuccessProductResonse(new HashMap<String, Object>());
		} catch (ServiceInventoryException e){
			String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
			if(errorcode != null) {
				if(errorcode.equals("core.1011")) {
					throw new ServiceInventoryException(400, "011011", "Bad token", "TMN-PRODUCT");
				} else if(errorcode.equals("core.1014")) {
					throw new ServiceInventoryException(400, "011014", "Bad password", "TMN-PRODUCT");
				} else if(errorcode.equals("core.2010")) {
					throw new ServiceInventoryException(400, "012010", "Pin access denied", "TMN-PRODUCT");
				}
			}
			throw e;
		}
	}
	
	@RequestMapping(value = "/profile/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse updateProfile(@PathVariable(value = "accessToken") String accessTokenID,
			@RequestBody Map<String, String> request) {
		
		String fullname = request.get("fullname");	
		
		TmnProfile tmnProfile = profileService.changeFullname(accessTokenID, fullname);
		String profileImageURL = profileImageManager.generateProfileImageURL(accessTokenID, tmnProfile.getImageFileName());
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("email", tmnProfile.getEmail());
		data.put("fullname", tmnProfile.getFullname());
		data.put("mobileNumber", tmnProfile.getMobileNumber());
		data.put("currentBalance", tmnProfile.getBalance().toString());
		data.put("hasPassword", tmnProfile.getHasPassword());
		data.put("hasPin", tmnProfile.getHasPin());
		data.put("imageURL", profileImageURL);
		
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
