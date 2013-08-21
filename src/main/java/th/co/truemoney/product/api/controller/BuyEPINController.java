package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.EPINConfigurationManager;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value = "/buy/e-pin")
public class BuyEPINController extends BaseController {
	
	@Autowired
	private BuyProductService buyProductService;
	
	@Autowired
	private TmnProfileService profileService;
	
	@Autowired
	private TransactionAuthenService authService;
	
	@Autowired
	private EPINConfigurationManager epinConfigurationManager;
	
	@RequestMapping(value = "/draft/verifyAndCreate/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse createBuyEpinDraft (
			@PathVariable String accessToken,
			@RequestBody Map<String, String> request)
		throws ServiceInventoryException {

		List<String> priceList = epinConfigurationManager.getEpinPrice();
		
		String recipientMobileNumber = request.get("recipientMobileNumber");
		String amount = request.get("amount").replace(",", "");
		BigDecimal epinAmount = new BigDecimal(amount);
		
		if(!priceList.contains(amount)) {
			throw new InvalidParameterException("5000");
		}
		
		if(!ValidateUtil.checkMobileNumber(recipientMobileNumber)){
			throw new InvalidParameterException("40001");
		}
		
		BuyProductDraft draft =  buyProductService.createAndVerifyBuyProductDraft("epin_c", recipientMobileNumber, epinAmount, accessToken);
        BuyProduct buyProduct = draft.getBuyProductInfo();
        
		OTP otp = authService.requestOTP(draft.getID(), accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("draftTransactionID", draft.getID());
		data.put("mobileNumber", otp.getMobileNumber());
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("amount", buyProduct.getAmount());

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/confirm/{draftTransactionID}/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse confirmBuyEpinOTP (
			@PathVariable String draftTransactionID, 
			@PathVariable String accessToken,
			@RequestBody Map<String, String> request) 
		throws ServiceInventoryException {
		
		OTP otp = new OTP();
		otp.setMobileNumber(request.get("mobileNumber"));
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		
		Status status = authService.verifyOTP(draftTransactionID, otp, accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", status.getStatus());
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/{draftTransactionID}/status/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse checkStatus(
			@PathVariable String draftTransactionID, 
			@PathVariable String accessToken)
		throws ServiceInventoryException {
	
		BuyProductTransaction.Status status = buyProductService.getBuyProductStatus(draftTransactionID, accessToken); 
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}
}
