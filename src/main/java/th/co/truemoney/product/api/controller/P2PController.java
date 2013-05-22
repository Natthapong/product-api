package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
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

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;

@Controller
@RequestMapping(value = "/transfer")
public class P2PController extends BaseController {

	@Autowired
	private P2PTransferService p2pTransferService;

	@Autowired
	private TransactionAuthenService authService;

	@Autowired
	private TmnProfileService profileService;

	@RequestMapping(value = "/draft-transaction/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse createP2PTransferDraft(@PathVariable String accessToken,
			@RequestBody Map<String, String> request)throws ServiceInventoryException {

		BigDecimal amount = new BigDecimal(request.get("amount").replace(",", ""));
		String mobileNumber = request.get("mobileNumber");

		if(!ValidateUtil.checkMobileNumber(mobileNumber)){
			throw new InvalidParameterException("40001");
		}

		P2PTransferDraft transaction = p2pTransferService.createAndVerifyTransferDraft(mobileNumber, amount, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", transaction.getMobileNumber());
		data.put("recipientName", transaction.getFullname());
		data.put("amount", transaction.getAmount());
		data.put("draftTransactionID", transaction.getID());


		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/send-otp/{accessToken}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse verifyTransfer(@PathVariable String draftTransactionID, @PathVariable String accessToken)throws ServiceInventoryException {

		OTP otp = authService.requestOTP(draftTransactionID, accessToken);
		P2PTransferDraft transaction = p2pTransferService.getTransferDraftDetails(draftTransactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("mobileNumber", String.valueOf(otp.getMobileNumber()).replaceFirst(
				"(\\d{3})(\\d{3})(\\d)", "$1-$2-$3"));
		data.put("amount", transaction.getAmount());
		data.put("draftTransactionID", transaction.getID());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/{accessToken}", method = RequestMethod.POST)
	@ResponseBody
	public ProductResponse confirmTransferOTP(@PathVariable String transactionID, @PathVariable String accessToken, @RequestBody Map<String, String> request)
			throws ServiceInventoryException {
		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));
		P2PTransferDraft.Status transaction = authService.verifyOTP(transactionID, otp, accessToken);
		p2pTransferService.performTransfer(transactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", transaction.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/status/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse checkStatus(@PathVariable String transactionID, @PathVariable String accessToken)throws ServiceInventoryException {
		P2PTransferTransaction.Status status = p2pTransferService.getTransferringStatus(transactionID, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("transferStatus", status.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/transaction/{transactionID}/{accessToken}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getTransferDetail(@PathVariable String transactionID, @PathVariable String accessToken)throws ServiceInventoryException {
		P2PTransferTransaction transaction = p2pTransferService.getTransactionResult(transactionID, accessToken);

		P2PTransactionConfirmationInfo info = transaction.getConfirmationInfo();
		P2PTransferDraft draftTxn = transaction.getDraftTransaction();

		BigDecimal balance = this.profileService.getEwalletBalance(accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", draftTxn.getMobileNumber());
		data.put("amount", draftTxn.getAmount());
		data.put("recipientName", draftTxn.getFullname());
		data.put("transactionID", info.getTransactionID());
		data.put("transactionDate", info.getTransactionDate());
		data.put("currentBalance", balance);

		return this.responseFactory.createSuccessProductResonse(data);
	}

}
