package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value = "/transfer")
public class P2PController extends BaseController {

	@Autowired
	private P2PTransferService p2pTransferService;
	
	@Autowired
	private TmnProfileService profileService;
	
	@RequestMapping(value = "/draft-transaction/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse createDraftTransaction(@PathVariable String accessToken, 
			@RequestBody Map<String, String> request)throws ServiceInventoryException {
		P2PDraftRequest p2pDraftRequest = new P2PDraftRequest();
		p2pDraftRequest.setAmount(new BigDecimal(request.get("amount").replace(",", "")));
		p2pDraftRequest.setMobileNumber(request.get("mobileNumber"));

		P2PDraftTransaction transaction = p2pTransferService.createDraftTransaction(p2pDraftRequest, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", transaction.getMobileNumber());
		data.put("recipientName", transaction.getFullname());
		data.put("amount", transaction.getAmount());
		data.put("draftTransactionID", transaction.getID());
		

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse verifyTransfer(@PathVariable String draftTransactionID, @PathVariable String accessToken)throws ServiceInventoryException {
		
		OTP otp = p2pTransferService.sendOTP(draftTransactionID, accessToken);
		P2PDraftTransaction transaction = p2pTransferService.getDraftTransactionDetails(draftTransactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("amount", transaction.getAmount());
		data.put("draftTransactionID", transaction.getID());

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/transaction/{draftTransactionID}/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse confirmTransferOTP(@PathVariable String draftTransactionID, @PathVariable String accessToken, @RequestBody Map<String, String> request)throws ServiceInventoryException {
		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setMobileNumber(request.get("mobileNumber"));
		P2PTransactionStatus transaction = p2pTransferService.createTransaction(draftTransactionID, otp, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", transaction.getP2pTransferStatus());
		//data.put("transactionID", transaction.get);

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/transaction/{transactionID}/status/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse checkStatus(@PathVariable String transactionID, @PathVariable String accessToken)throws ServiceInventoryException {
		P2PTransactionStatus status = p2pTransferService.getTransactionStatus(transactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", status.getP2pTransferStatus());
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/transaction/{transactionID}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getTransferDetail(@PathVariable String transactionID, @PathVariable String accessToken, @RequestBody Map<String, String> request)throws ServiceInventoryException {
		P2PTransaction transaction = p2pTransferService.getTransactionResult(transactionID, accessToken);

		P2PTransactionConfirmationInfo info = transaction.getConfirmationInfo();
	
		BigDecimal balance = this.profileService.getEwalletBalance(accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", transaction.getMobileNumber());
		data.put("amount", transaction.getAmount());
		data.put("recipientName", transaction.getFullname());
		data.put("transactionID", info.getTransactionDate());
		data.put("transactionDate", info.getTransactionDate());
		data.put("currentBalance", balance);

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
}
