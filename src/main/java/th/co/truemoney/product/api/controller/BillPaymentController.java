package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.BillUtil;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController extends BaseController {

	@Autowired
	public BillPaymentService billPaymentService;

	@Autowired
	TmnProfileService profileService;

	@Autowired
	MessageManager messageManager;

	@RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillInformation(
			@PathVariable String barcode,
			@PathVariable String accessTokenID) {

		Bill billPaymentInfo = billPaymentService.retrieveBillInformation(barcode, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("target", billPaymentInfo.getTarget());
		data.put("logoURL", billPaymentInfo.getLogoURL());
		data.put("titleTH", billPaymentInfo.getTitleTH());
		data.put("titleEN", billPaymentInfo.getTitleEN());

		data.put("ref1TitleTH", billPaymentInfo.getRef1TitleTH());
		data.put("ref1TitleEN", billPaymentInfo.getRef1TitleEN());
		data.put("ref1", billPaymentInfo.getRef1());

		data.put("ref2TitleTH", billPaymentInfo.getRef2TitleTH());
		data.put("ref2TitleEN", billPaymentInfo.getRef2TitleEN());
		data.put("ref2", billPaymentInfo.getRef2());

		data.put("amount", billPaymentInfo.getAmount());
		data.put("serviceFee", billPaymentInfo.getServiceFee());
		data.put("partialPaymentAllow", billPaymentInfo.getPartialPayment());
		data.put("isTrueCorpBill", BillUtil.isTrueCorpBill(billPaymentInfo.getTarget()));

		data.put("minAmount", billPaymentInfo.getMinAmount());
		data.put("maxAmount", billPaymentInfo.getMaxAmount());
		
		data.put("serviceFeeType", billPaymentInfo.getServiceFee().getFeeRateType());
		data.put("serviceFee", billPaymentInfo.getServiceFee().getFeeRate());
		data.put("sourceOfFundFee", prepareData(billPaymentInfo.getSourceOfFundFees()));
		data.put("billID", billPaymentInfo.getID());

		return this.responseFactory.createSuccessProductResonse(data);

	}

	@RequestMapping(value = "/create/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse createBillPayment(
			@PathVariable String accessTokenID,
			@RequestBody Map<String, Object> request) {

		String billID = (String)request.get("billID");
		Double amount = (Double)request.get("amount");

		BillPaymentDraft bill = this.billPaymentService.verifyPaymentAbility(billID, new BigDecimal(amount), accessTokenID);
		Bill billInfo = bill.getBillInfo();
		BigDecimal totalFee = BillUtil.calculateTotalFee(billInfo.getAmount(), billInfo.getServiceFee(), billInfo.getSourceOfFundFees());
		BigDecimal totalAmount = bill.getAmount().add(totalFee);
		
		OTP otp = this.billPaymentService.sendOTP(bill.getID(), accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("otpRefCode", otp.getReferenceCode());
		data.put("mobileNumber", otp.getMobileNumber());
		data.put("billID", bill.getID());
		data.put("totalAmount", totalAmount);
		
		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/{billID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmBillPayment(@PathVariable String billID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));

		BillPaymentDraft.Status sts = billPaymentService.confirmBill(billID, otp, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", sts.getStatus());
		data.put("billPaymentID", billID); //billPaymentID has the same value as billID

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/{billPaymentID}/status/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentStatus(
			@PathVariable String billPaymentID,
			@PathVariable String accessTokenID) {

		BillPaymentTransaction.Status sts = this.billPaymentService.getBillPaymentStatus(billPaymentID, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billPaymentStatus", sts.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/{billPaymentID}/details/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentDetail(
			@PathVariable String billPaymentID,
			@PathVariable String accessTokenID) {

		BillPaymentTransaction tnx = this.billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);

		BillPaymentConfirmationInfo confirmedInfo = tnx.getConfirmationInfo();

		Bill billInfo = tnx.getDraftTransaction().getBillInfo();
		
		BigDecimal amount = billInfo.getAmount();
		BigDecimal totalFee = BillUtil.calculateTotalFee(billInfo.getAmount(), billInfo.getServiceFee(), billInfo.getSourceOfFundFees());
		BigDecimal totalAmount = amount.add(totalFee);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("target", billInfo.getTarget());
		data.put("logoURL", billInfo.getLogoURL());
		data.put("titleTH", billInfo.getTitleTH());
		data.put("titleEN", billInfo.getTitleEN());

		data.put("ref1TitleTH", billInfo.getRef1TitleTH());
		data.put("ref1TitleEN", billInfo.getRef1TitleEN());
		data.put("ref1", billInfo.getRef1());

		data.put("ref2TitleTH", billInfo.getRef2TitleTH());
		data.put("ref2TitleEN", billInfo.getRef2TitleEN());
		data.put("ref2", billInfo.getRef2());

		data.put("amount", amount);
		data.put("totalFee", totalFee);
		
		data.put("totalAmount", totalAmount);
		data.put("sourceOfFund", "eWallet"); //TODO Hard code!!!

		data.put("transactionID", confirmedInfo.getTransactionID());
		data.put("transactionDate", confirmedInfo.getTransactionDate());

		data.put("remarkEN", messageManager.getMessageEn("payment.bill.remark"));
		data.put("remarkTH", messageManager.getMessageTh("payment.bill.remark"));

		BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);
		data.put("currentEwalletBalance", currentBalance);

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	private List<JSONObject> prepareData(BillPaySourceOfFund[] sourceOfFundFees) {
		List<JSONObject> realData = new ArrayList<JSONObject>();

		for (BillPaySourceOfFund billPaySourceOfFund : sourceOfFundFees) {
			JSONObject returnData = new JSONObject();
			//TODO warning hard code response eWallet only
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
