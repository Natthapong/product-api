package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.BillResponse;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController extends BaseController {

	@Autowired
    BillPaymentService billPaymentService;

	@Autowired
	TransactionAuthenService authService;

	@Autowired
	TmnProfileService profileService;

	@Autowired
	MessageManager messageManager;
	
	Logger logger = Logger.getLogger(BillPaymentController.class);
	
	@RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillInformation(
			@PathVariable String barcode,
			@PathVariable String accessTokenID) {

		StopWatch timer = new StopWatch("getBillInformation ("+accessTokenID+")");
		timer.start();

		Bill bill = billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setBill(bill)
										.buildBillInfoResponse();
		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}

	@RequestMapping(value = "/create/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse createBillPayment(
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		StopWatch timer = new StopWatch("createBillPayment ("+accessTokenID+")");
		timer.start();

		String billID = (String)request.get("billID");
		BigDecimal amount = new BigDecimal(request.get("amount").replace(",", ""));

		BillPaymentDraft paymentDraft = this.billPaymentService.verifyPaymentAbility(billID, amount, accessTokenID);
		OTP otp = this.authService.requestOTP(paymentDraft.getID(), accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setOTP(otp)
										.setPaymentDraft(paymentDraft)
										.buildBillCreateResponse();
		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
		}

	@RequestMapping(value = "/{billID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmBillPayment(
			@PathVariable String billID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		StopWatch timer = new StopWatch("confirmBillPayment ("+accessTokenID+")");
		timer.start();
		
		BillPaymentDraft draft = billPaymentService.getBillPaymentDraftDetail(billID, accessTokenID);
		BillPaymentDraft.Status status = draft.getStatus();
		
		if (status != Status.OTP_CONFIRMED) {
			String otpStr = request.get("otpString");
			String otpRef = request.get("otpRefCode");
			String mobile = request.get("mobileNumber");
			
			OTP otp = new OTP(mobile, otpRef, otpStr);
			status = authService.verifyOTP(billID, otp, accessTokenID);
		}
		
		billPaymentService.performPayment(billID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", status.getStatus());
		data.put("billPaymentID", billID); //billPaymentID has the same value as billID

		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}

	@RequestMapping(value = "/{billPaymentID}/status/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentStatus(
			@PathVariable String billPaymentID,
			@PathVariable String accessTokenID) {
		
		StopWatch timer = new StopWatch("getBillPaymentStatus ("+accessTokenID+")");
		timer.start();

		BillPaymentTransaction.Status sts = this.billPaymentService.getBillPaymentStatus(billPaymentID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", sts.getStatus());

		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}

	@RequestMapping(value = "/{billPaymentID}/details/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentDetail(
			@PathVariable String billPaymentID,
			@PathVariable String accessTokenID) {

		StopWatch timer = new StopWatch("getBillPaymentDetail ("+accessTokenID+")");
		timer.start();

		BillPaymentTransaction txn = this.billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);
	
		BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setPaymentTransaction(txn)
										.setWalletBalance(currentBalance)
										.buildBillPaymentDetailResponse();
		
		Bill bill = txn.getDraftTransaction().getBillInfo();
		String target = Utils.removeSuffix(bill.getTarget());
		if ("tmvh".equals(target) || "trmv".equals(target)) {
			// remark message that display at the bottom of receipt
			data.put("remarkEn", messageManager.getMessageEn("payment.bill.remark"));
			data.put("remarkTh", messageManager.getMessageTh("payment.bill.remark"));
		}

		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}
	
	@RequestMapping(value = "/favorite/verify/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse verifyAndGetBillPaymentFavoriteInfo(
			@PathVariable String accessTokenID, @RequestBody Map<String,String> request) {
		
		StopWatch timer = new StopWatch("verifyAndGetBillPaymentFavoriteInfo for favorite bill ("+accessTokenID+")");
		timer.start();
		
		BigDecimal amount = new BigDecimal(request.get("amount").replace(",", ""));
		String billCode = request.get("billCode");
		String ref1 = request.get("ref1");
		
		if (isEmptyString(billCode) || isEmptyString(ref1)) { 
			throw new InvalidParameterException("50010"); 
		}
		
		Bill bill = billPaymentService.retrieveBillInformationWithBillCode(
							billCode, ref1, amount, accessTokenID);
		
		BillPaymentDraft paymentDraft = billPaymentService.verifyPaymentAbility(
							bill.getID(), amount, accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setBill(bill)
										.setPaymentDraft(paymentDraft)
										.buildBillFavoriteResponse();
		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}

	private ProductResponse createResponse(Map<String, Object> data) {
		return this.responseFactory.createSuccessProductResonse(data);
	}

	public void setBillPaymentService(BillPaymentService billPaymentService) {
		this.billPaymentService = billPaymentService;
	}

	public void setAuthService(TransactionAuthenService authService) {
		this.authService = authService;
	}
	
	private boolean isEmptyString(String str) {
		return ! StringUtils.hasText(str);
	}
	
}

