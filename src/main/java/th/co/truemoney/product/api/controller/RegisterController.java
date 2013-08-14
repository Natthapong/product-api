package th.co.truemoney.product.api.controller;

import static th.co.truemoney.product.api.util.ProductResponseFactory.PRODUCT_NAMESPACE;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.OTPBean;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.SecurityManager;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value = "/ewallet")
public class RegisterController extends BaseController {

    @Autowired
    private TmnProfileService profileService;

    @Autowired
    private SecurityManager securityManager;

    @RequestMapping(value = "/profiles/validate-email", method = RequestMethod.POST)
    @ResponseBody
    public ProductResponse validateEmail(
            @RequestBody Map<String, String> request)
            throws ServiceInventoryException {

        String email = request.get("email");
        if (!ValidateUtil.checkEmail(email)) {
            throw new InvalidParameterException("40000");
        }
        String returnData = verifyEmail(email);

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
        tmnProfile.setEmail(email.toLowerCase());
        tmnProfile.setFullname(request.get("fullname"));
        tmnProfile.setMobileNumber(request.get("mobileNumber"));
        String password = securityManager.encryptRSA(request.get("password"));
        tmnProfile.setPassword(password);
        tmnProfile.setThaiID(request.get("thaiID"));

        OTP returnData = requestCreateProfile(tmnProfile);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("otpRefCode", returnData.getReferenceCode());
        data.put("mobileNumber", returnData.getMobileNumber());

        return this.responseFactory.createSuccessProductResonse(data);
    }
    
    @RequestMapping(value = "/profiles/verify-otp", method = RequestMethod.POST)
    public @ResponseBody ProductResponse confirmCreateProfile(
    	   @RequestBody @Valid OTPBean otpBean) throws ServiceInventoryException {

        confirmCreateProfile(otpBean.toOTPObj());
        
        return this.responseFactory.createSuccessProductResonse(Collections.<String, Object>emptyMap());
    }
    
    private static final Integer HTTP_BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;
    
    private static final String INVALID_PARAMETER = HTTP_BAD_REQUEST.toString();
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody
	ProductResponse handleInvalidParameterExceptions(
			MethodArgumentNotValidException exception, 
			HttpServletResponse response) {
		response.setStatus(HTTP_BAD_REQUEST);
		
		String errorMessage = "Invalid " + exception.getBindingResult().getFieldError().getField();
		
		return responseFactory.createErrorProductResponse(
				new ServiceInventoryException(
						HTTP_BAD_REQUEST, INVALID_PARAMETER, errorMessage, PRODUCT_NAMESPACE));
	}
    
    private String verifyEmail(String email) {
        try {
            return profileService.validateEmail(MOBILE_APP_CHANNEL_ID, email.toLowerCase());
        } catch (ServiceInventoryException e) {
            String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
            if (errorcode.equals("core.18")) {
                throw new ServiceInventoryException(400, "10000", "Email is already in used.", "TMN-PRODUCT");
            }
            throw e;
        }
    }

    private OTP requestCreateProfile(TmnProfile tmnProfile) {

        try {
            return profileService.createProfile(MOBILE_APP_CHANNEL_ID, tmnProfile);
        } catch (ServiceInventoryException e) {
            String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
            if (errorcode.equals("core.18")) {
                throw new ServiceInventoryException(400, "10001",
                        "Mobile number is already in used.", "TMN-PRODUCT");
            }
            throw e;
        }
    }

    private void confirmCreateProfile(OTP otp) {
        try {
            profileService.confirmCreateProfile(MOBILE_APP_CHANNEL_ID, otp);
        } catch (ServiceInventoryException e) {
            String errorcode = String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode());
            if (!errorcode.equals("next.18")) {
                throw e;
            }

        }
    }
    
}
