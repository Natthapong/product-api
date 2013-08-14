package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.ProfileImageManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@Controller
@RequestMapping(value = "/transfer")
public class P2PController extends BaseController {

	@Autowired
	private TransactionAuthenService authService;

	@Autowired
	private P2PTransferService transferService;

	@Autowired
	private TmnProfileService profileService;
	
	@Autowired
	private ProfileImageManager profileImageManager;

	@RequestMapping(value = "/draft-transaction/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse createP2PTransferDraft(
			@PathVariable String accessToken,
			@RequestBody Map<String, String> request)
		throws ServiceInventoryException {

		String amount = request.get("amount").replace(",", "");
		String mobileNumber = request.get("mobileNumber");
		BigDecimal inputAmount = new BigDecimal(amount);

		if(!ValidateUtil.checkMobileNumber(mobileNumber)){
			throw new InvalidParameterException("40001");
		}

		P2PTransferDraft transaction = transferService.createAndVerifyTransferDraft(mobileNumber, inputAmount, accessToken);
		String profileImageURL = profileImageManager.generateProfileImageURL(accessToken, transaction.getImageFileName());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", Utils.formatMobileNumber(transaction.getMobileNumber()));
		data.put("recipientName", transaction.getFullname());
		data.put("recipientImageURL", profileImageURL);
		data.put("draftTransactionID", transaction.getID());
		data.put("amount", transaction.getAmount());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/send-otp/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse verifyTransfer(
			@PathVariable String draftTransactionID, 
			@PathVariable String accessToken,
			@RequestBody Map<String, String> request)
		throws ServiceInventoryException {
		
		String personalMessage = request.get("personalMessage");
		
		if(StringUtils.hasLength(personalMessage) == true){
			transferService.setPersonalMessage(draftTransactionID, personalMessage, accessToken);
		}
		
		OTP otp = authService.requestOTP(draftTransactionID, accessToken);
		P2PTransferDraft transaction = transferService.getTransferDraftDetails(draftTransactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("amount", transaction.getAmount());
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("mobileNumber", otp.getMobileNumber());
		data.put("draftTransactionID", transaction.getID());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse confirmTransferOTP(
			@PathVariable String transactionID, 
			@PathVariable String accessToken, 
			@RequestBody Map<String, String> request)
		throws ServiceInventoryException {
		OTP otp = new OTP();
		otp.setMobileNumber(request.get("mobileNumber"));
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		Status status = authService.verifyOTP(transactionID, otp, accessToken);
		transferService.performTransfer(transactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/status/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse checkStatus(
			@PathVariable String transactionID, 
			@PathVariable String accessToken)
		throws ServiceInventoryException {
		
		P2PTransferTransaction.Status status = transferService.getTransferringStatus(transactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getTransferDetail(
			@PathVariable String transactionID, 
			@PathVariable String accessToken) 
		throws ServiceInventoryException {
		
		P2PTransferTransaction transaction = transferService.getTransactionResult(transactionID, accessToken);

		P2PTransactionConfirmationInfo info = transaction.getConfirmationInfo();
		P2PTransferDraft draftTxn = transaction.getDraftTransaction();
		
		String profileImageURL = profileImageManager.generateProfileImageURL(accessToken, draftTxn.getImageFileName());
		
		BigDecimal balance = this.profileService.getEwalletBalance(accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("currentBalance", balance);

		data.put("amount", draftTxn.getAmount());
		data.put("recipientName", draftTxn.getFullname());
		data.put("recipientImageURL", profileImageURL);
		data.put("transactionID", info.getTransactionID());
		data.put("transactionDate", info.getTransactionDate());
		data.put("mobileNumber", Utils.formatMobileNumber(draftTxn.getMobileNumber()));
		data.put("personalMessage", draftTxn.getMessage());
		return this.responseFactory.createSuccessProductResonse(data);
	}

}
