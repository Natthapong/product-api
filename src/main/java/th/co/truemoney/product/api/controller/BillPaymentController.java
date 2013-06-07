package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Date;
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
import th.co.truemoney.product.api.util.BillReferenceUtil;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

/**
 * Transaction States:
 *   1. Bill
 *   2. BillPaymentDraft
 * 	 3. BillPayTransaction
 *   4. BillPaymentConfirmation
 *   
 *   BillPaymentTransaction.ID == BillPaymentDraft.ID
 */

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController extends BaseController {

	@Autowired
    private BillPaymentService billPaymentService;

	@Autowired
	private TransactionAuthenService authService;

	@Autowired
	private TmnProfileService profileService;

	@Autowired
	private MessageManager messageManager;
	
	private Logger logger = Logger.getLogger(BillPaymentController.class);
	
	@RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillInformation(
			@PathVariable String barcode,
			@PathVariable String accessTokenID) {

		StopWatch timer = new StopWatch("getBillInformation ("+accessTokenID+")");
		timer.start();
		Bill bill;
		
		try{
			bill = billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);
		}catch(ServiceInventoryException e){
			if("TMN-SERVICE-INVENTORY".equals(e.getErrorNamespace()) && "1012".equals(e.getErrorCode())){
				String targetTitle = getTargetTitle(Utils.removeSuffix(e.getData().get("target").toString()));
				e.setErrorCode("80000");
				e.setErrorNamespace("TMN-PRODUCT");
				Date dueDate = new Date((Long)e.getData().get("dueDate"));
				e.getData().put("dueDate",Utils.formatDate(dueDate));
				e.getData().put("targetTitle",targetTitle);
				throw e;
			}else{
				throw e;
			}
		}
		
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
		BigDecimal inputAmount = new BigDecimal(request.get("amount").replace(",", ""));
		
		BillPaymentDraft paymentDraft = this.billPaymentService.verifyPaymentAbility(billID, inputAmount, accessTokenID);
		
		OTP otp = this.authService.requestOTP(paymentDraft.getID(), accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setOTP(otp)
										.setPaymentDraft(paymentDraft)
										.buildBillCreateResponse();
		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
		}

	@RequestMapping(value = "/{draftID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmBillPayment(
			@PathVariable String draftID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		StopWatch timer = new StopWatch("confirmBillPayment ("+accessTokenID+")");
		timer.start();
		
		BillPaymentDraft draft = billPaymentService.getBillPaymentDraftDetail(draftID, accessTokenID);
		BillPaymentDraft.Status draftStatus = draft.getStatus();
		
		if (draftStatus != Status.OTP_CONFIRMED) {
			String otpStr = request.get("otpString");
			String otpRef = request.get("otpRefCode");
			String mobile = request.get("mobileNumber");
			
			OTP otp = new OTP(mobile, otpRef, otpStr);
			authService.verifyOTP(draftID, otp, accessTokenID);
		}
		
		BillPaymentTransaction.Status transactionStatus = billPaymentService.performPayment(draftID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", transactionStatus.getStatus());
		data.put("billPaymentID", draftID); //billPaymentID has the same value as draftID

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
		
		if(ValidateUtil.isMobileNumber(bill.getRef1())){
			String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
			data.put("ref1", formattedMobileNumber);
		}else if(ValidateUtil.isTelNumber(bill.getRef1())){
			String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
			data.put("ref1", formattedTelNumber);
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
		String inputAmount = request.get("amount");
		
		if (ValidateUtil.isEmpty(inputAmount)) {
			throw new InvalidParameterException("60000");
		}
		
		BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));
		String billCode = request.get("billCode");
		String ref1 = request.get("ref1");
		String ref2 = request.containsKey("ref2") ? request.get("ref2") : "";
		
		if (isEmptyString(billCode) || isEmptyString(ref1)) { 
			throw new InvalidParameterException("50010"); 
		}
		
		Bill bill = billPaymentService.retrieveBillInformationWithBillCode(
							billCode, ref1, ref2, amount, accessTokenID);
		
		BillPaymentDraft paymentDraft = billPaymentService.verifyPaymentAbility(
							bill.getID(), amount, accessTokenID);
		
		Map<String, Object> data = BillResponse.builder()
										.setPaymentDraft(paymentDraft)
										.buildBillFavoriteResponse();
		
		if(ValidateUtil.isMobileNumber(bill.getRef1())){
			String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
			data.put("ref1", formattedMobileNumber);
		}else if(ValidateUtil.isTelNumber(bill.getRef1())){
			String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
			data.put("ref1", formattedTelNumber);
		}
		
		timer.stop();
		logger.info(timer.shortSummary());

		return createResponse(data);
	}
	
	@RequestMapping(value = "/info/{billCode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getKeyInBillInformation(
			@PathVariable String accessTokenID, @PathVariable String billCode) {
		BillReferenceUtil billReferenceUtil = new BillReferenceUtil();
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, String> placeHolderMessages = billReferenceUtil.getBillInfoResponse(Utils.removeSuffix(billCode));
		if (placeHolderMessages == null) {
			throw new ServiceInventoryException(400, "30000", "", "TMN-PRODUCT");
		}else{
			data.putAll(placeHolderMessages);
			data.put("target", billCode);
		
			if(data.get("ref2TitleTh").equals("")){
				data.remove("ref2TitleTh");
				data.remove("ref2TitleEn");
			}
		}
		return createResponse(data);
	}
	
	@RequestMapping(value = "/key-in/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse getKeyInBillPayment(
			@PathVariable String accessTokenID, @RequestBody Map<String,String> request) {
		String inputAmount = request.get("amount");
		String target = request.get("target");
		if (ValidateUtil.isEmpty(inputAmount)) {
			throw new InvalidParameterException("60000");
		}
		
		Bill bill;
		try{
			bill = billPaymentService.retrieveBillInformationWithKeyin(target, accessTokenID);
		}catch(ServiceInventoryException e){
			if("PCS.PCS-30024".equals(String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode()))){
				if("tmvh".equals(Utils.removeSuffix(target)) || "trmv".equals(Utils.removeSuffix(target))){
					throw new ServiceInventoryException(500,"70000","","TMN-PRODUCT");
				}
			}
			throw e;
		}

		BillPaymentDraft paymentDraft = billPaymentService.verifyPaymentAbility(bill.getID(), bill.getAmount(), accessTokenID);
		Map<String, Object> data = BillResponse.builder()
				.setPaymentDraft(paymentDraft)
				.buildBillInfoResponse();
				
		String formattedMobileNumber = parseMobileAndTelNumber(paymentDraft.getBillInfo().getRef1());
		data.put("ref1", formattedMobileNumber);				

		return createResponse(data);
	}
	
	@RequestMapping(value = "/inquiry/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse getBillOnlineInformation(@PathVariable String accessTokenID,@RequestBody Map<String, String> request){
		// get 
		String target = request.get("target");
		String ref1 = request.get("ref1");
		String ref2 = request.containsKey("ref2") ? request.get("ref2") : "";
		
		OutStandingBill outStandingBill = new OutStandingBill();
		Bill bill = new Bill();
		try{
			outStandingBill = billPaymentService.retrieveBillOutStandingOnline(target, ref1, ref2, accessTokenID);
			//bill = billPaymentService.retrieveBillInformationWithBillCode(target, ref1, outStandingBill.getOutStandingBalance(), accessTokenID);
			bill.setAmount(outStandingBill.getOutStandingBalance());
			bill.setDueDate(outStandingBill.getDueDate());
			bill.setRef1(outStandingBill.getRef1() != null ? outStandingBill.getRef1() : ref1);
			bill.setRef2(outStandingBill.getRef2() != null ? outStandingBill.getRef2() : ref2);
			bill.setTarget(target);
		}catch(ServiceInventoryException e){
			if("PCS.PCS-30024".equals(String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode()))){
				if("tmvh".equals(Utils.removeSuffix(target)) || "trmv".equals(Utils.removeSuffix(target))){
					throw new ServiceInventoryException(500,"70000","","TMN-PRODUCT");
				} else if("mea".equals(Utils.removeSuffix(target))) {
					throw new ServiceInventoryException(500,"90000","","TMN-PRODUCT");
				}
			}
			throw e;
		}

		Map<String, Object> data = BillResponse.builder().setBill(bill).buildBillInfoResponse();		
		String formattedMobileNumber = parseMobileAndTelNumber(bill.getRef1());
		data.put("ref1", formattedMobileNumber);
		return createResponse(data);
	}
	
	private String parseMobileAndTelNumber(String mobileNumber) {
		String formattedNumber = mobileNumber;
		if(ValidateUtil.isMobileNumber(mobileNumber)){
			formattedNumber = Utils.formatMobileNumber(mobileNumber);			
		}else if(ValidateUtil.isTelNumber(mobileNumber)){
			formattedNumber = Utils.formatTelNumber(mobileNumber);			
		}
		return formattedNumber;
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
	
	public void setProfileService(TmnProfileService profileService) {
		this.profileService = profileService;
	}

	private boolean isEmptyString(String str) {
		return ! StringUtils.hasText(str);
	}
	
	private String getTargetTitle(String target){
		String result = "";
		if("mea".equals(target)){
			result = "การไฟฟ้านครหลวง";
		}else if("water".equals(target)){
			result = "การประปานครหลวง";
		}
		return result;
	}
	
}

