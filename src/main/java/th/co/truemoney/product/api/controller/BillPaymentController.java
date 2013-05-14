package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
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

		Bill billPaymentInfo = billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("target", billPaymentInfo.getTarget());
		data.put("logoURL", billPaymentInfo.getLogoURL());
		
		if(Utils.isTrueCorpBill(billPaymentInfo.getTarget())){
			data.put("titleTh", "");
			data.put("titleEn", "");
		}else{
			data.put("titleTh", billPaymentInfo.getTitleTH());
			data.put("titleEn", billPaymentInfo.getTitleEN());
		}

		data.put("ref1TitleTh", billPaymentInfo.getRef1TitleTH());
		data.put("ref1TitleEn", billPaymentInfo.getRef1TitleEN());
		data.put("ref1", billPaymentInfo.getRef1());

		data.put("ref2TitleTh", billPaymentInfo.getRef2TitleTH());
		data.put("ref2TitleEn", billPaymentInfo.getRef2TitleEN());
		data.put("ref2", billPaymentInfo.getRef2());

		data.put("amount", billPaymentInfo.getAmount());
		data.put("serviceFee", billPaymentInfo.getServiceFee());
		data.put("partialPaymentAllow", billPaymentInfo.getPartialPayment());
		data.put("isTrueCorpBill", Utils.isTrueCorpBill(billPaymentInfo.getTarget()));

		data.put("minAmount", billPaymentInfo.getMinAmount());
		data.put("maxAmount", billPaymentInfo.getMaxAmount());

		data.put("serviceFeeType", billPaymentInfo.getServiceFee().getFeeRateType());
		data.put("serviceFee", billPaymentInfo.getServiceFee().getFeeRate());
		data.put("sourceOfFundFee", prepareData(billPaymentInfo.getSourceOfFundFees()));
		data.put("billID", billPaymentInfo.getID());

		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
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

		BillPaymentDraft bill = this.billPaymentService.verifyPaymentAbility(billID, amount, accessTokenID);
		Bill billInfo = bill.getBillInfo();

		BigDecimal totalFee = Utils.calculateTotalFee(bill.getAmount(), billInfo.getServiceFee(), billInfo.getSourceOfFundFees());
		BigDecimal totalAmount = bill.getAmount().add(totalFee);

		OTP otp = this.authService.requestOTP(bill.getID(), accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("mobileNumber", otp.getMobileNumber());
		data.put("billID", bill.getID());
		data.put("totalAmount", totalAmount);

		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
		}

	@RequestMapping(value = "/{billID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmBillPayment(@PathVariable String billID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		StopWatch timer = new StopWatch("confirmBillPayment ("+accessTokenID+")");
		timer.start();

		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));

		BillPaymentDraft.Status sts = authService.verifyOTP(billID, otp, accessTokenID);
		billPaymentService.performPayment(billID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", sts.getStatus());
		data.put("billPaymentID", billID); //billPaymentID has the same value as billID

		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
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

		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
	}

	@RequestMapping(value = "/{billPaymentID}/details/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentDetail(
			@PathVariable String billPaymentID,
			@PathVariable String accessTokenID) {

		StopWatch timer = new StopWatch("getBillPaymentDetail ("+accessTokenID+")");
		timer.start();

		BillPaymentTransaction tnx = this.billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);

		BillPaymentConfirmationInfo confirmedInfo = tnx.getConfirmationInfo();

		Bill billInfo = tnx.getDraftTransaction().getBillInfo();

		BigDecimal amount = tnx.getDraftTransaction().getAmount();
		BigDecimal totalFee = Utils.calculateTotalFee(amount, billInfo.getServiceFee(), billInfo.getSourceOfFundFees());
		BigDecimal totalAmount = amount.add(totalFee);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("target", billInfo.getTarget());
		data.put("logoURL", billInfo.getLogoURL());
		data.put("titleTh", billInfo.getTitleTH());
		data.put("titleEn", billInfo.getTitleEN());

		data.put("ref1TitleTh", billInfo.getRef1TitleTH());
		data.put("ref1TitleEn", billInfo.getRef1TitleEN());
		data.put("ref1", billInfo.getRef1());

		data.put("ref2TitleTh", billInfo.getRef2TitleTH());
		data.put("ref2TitleEn", billInfo.getRef2TitleEN());
		data.put("ref2", billInfo.getRef2());

		data.put("amount", amount);
		data.put("totalFee", totalFee);

		data.put("totalAmount", totalAmount);
		data.put("sourceOfFund", "Wallet"); //TODO Hard code!!!

		data.put("transactionID", confirmedInfo.getTransactionID());
		data.put("transactionDate", confirmedInfo.getTransactionDate());

		data.put("remarkEn", messageManager.getMessageEn("payment.bill.remark"));
		data.put("remarkTh", messageManager.getMessageTh("payment.bill.remark"));

		BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);
		data.put("currentEwalletBalance", currentBalance);

		data.put("isFavoritable", String.valueOf(billInfo.isFavoritable()));
		data.put("isFavorited", String.valueOf(billInfo.isFavorited()));

		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
	}
	
	@RequestMapping(value = "/favorite/verify/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse verifyAndGetBillPaymentFavoriteInfo(
			@PathVariable String accessTokenID, @RequestBody Map<String,String> request) {
		
		StopWatch timer = new StopWatch("verifyAndGetBillPaymentFavoriteInfo for favorite bill ("+accessTokenID+")");
		timer.start();
		
		Bill billPaymentInfo = billPaymentService.retrieveBillInformationWithBillCode(
				request.get("billCode"), request.get("ref1"), new BigDecimal(request.get("amount")), accessTokenID);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billCode", billPaymentInfo.getTarget());
		data.put("logoURL", billPaymentInfo.getLogoURL());
		
		if(Utils.isTrueCorpBill(billPaymentInfo.getTarget())){
			data.put("titleTh", "");
			data.put("titleEn", "");
		}else{
			data.put("titleTh", billPaymentInfo.getTitleTH());
			data.put("titleEn", billPaymentInfo.getTitleEN());
		}

		data.put("ref1TitleTh", billPaymentInfo.getRef1TitleTH());
		data.put("ref1TitleEn", billPaymentInfo.getRef1TitleEN());
		data.put("ref1", billPaymentInfo.getRef1());

		data.put("ref2TitleTh", billPaymentInfo.getRef2TitleTH());
		data.put("ref2TitleEn", billPaymentInfo.getRef2TitleEN());
		data.put("ref2", billPaymentInfo.getRef2());

		data.put("amount", billPaymentInfo.getAmount());
		data.put("serviceFee", billPaymentInfo.getServiceFee());
		data.put("partialPaymentAllow", billPaymentInfo.getPartialPayment());

		data.put("minAmount", billPaymentInfo.getMinAmount());
		data.put("maxAmount", billPaymentInfo.getMaxAmount());

		data.put("serviceFeeType", billPaymentInfo.getServiceFee().getFeeRateType());
		data.put("serviceFee", billPaymentInfo.getServiceFee().getFeeRate());
		data.put("sourceOfFundFee", prepareData(billPaymentInfo.getSourceOfFundFees()));
		data.put("billPaymentID", billPaymentInfo.getID());
		
		ProductResponse response = this.responseFactory.createSuccessProductResonse(data);

		timer.stop();
		logger.info(timer.shortSummary());

		return response;
	}

	private List<JSONObject> prepareData(SourceOfFund[] sourceOfFundFees) {
		List<JSONObject> realData = new ArrayList<JSONObject>();

		for (SourceOfFund billPaySourceOfFund : sourceOfFundFees) {
			JSONObject returnData = new JSONObject();
			//TODO warning hard code response Wallet only
			if ("EW".equals(billPaySourceOfFund.getSourceType())) {
				returnData.put("sourceType", billPaySourceOfFund.getSourceType());
				returnData.put("sourceFee", billPaySourceOfFund.getFeeRate());
				returnData.put("sourceFeeType", billPaySourceOfFund.getFeeRateType());
				returnData.put("minSourceFeeAmount", billPaySourceOfFund.getMinFeeAmount());
				returnData.put("maxSourceFeeAmount", billPaySourceOfFund.getMaxFeeAmount());
				realData.add(returnData);
			}
		}
		return realData;
	}

}
