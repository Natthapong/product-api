package th.co.truemoney.product.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController extends BaseController {

	@Autowired
	public BillPaymentService billPaymentService;

	@RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody 
	ProductResponse getBillInformation(@PathVariable String barcode, @PathVariable String accessTokenID) {
		
		BillPaymentInfo billPaymentInfo = billPaymentService.getBillInformation(barcode, accessTokenID);  
		
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
		data.put("sourceOfFundFee", billPaymentInfo.getSourceOfFundFees()[0]);

		return this.responseFactory.createSuccessProductResonse(data);
		
	}
	
	@RequestMapping(value = "/invoice", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse createBillInvoice(
			@RequestParam String accessToken,
			@RequestBody BillPaymentInfo billPaymentInfo) {

		BillInvoice billInvoice = this.billPaymentService
				.createBillInvoice(billPaymentInfo, accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("target", billInvoice.getBillPaymentInfo().getTarget());
		data.put("logoURL", billInvoice.getBillPaymentInfo().getLogoURL());
		data.put("titleTH", billInvoice.getBillPaymentInfo().getTitleTH());		
		data.put("titleEN", billInvoice.getBillPaymentInfo().getTitleEN());
		
		data.put("ref1TitleTH", billInvoice.getBillPaymentInfo().getRef1TitleTH());
		data.put("ref1TitleEN", billInvoice.getBillPaymentInfo().getRef1TitleEN());
		data.put("ref1", billInvoice.getBillPaymentInfo().getRef1());
		
		data.put("ref2TitleTH", billInvoice.getBillPaymentInfo().getRef2TitleTH());
		data.put("ref2TitleEN", billInvoice.getBillPaymentInfo().getRef2TitleEN());
		data.put("ref2", billInvoice.getBillPaymentInfo().getRef2());
		
		data.put("amount", billInvoice.getBillPaymentInfo().getAmount());
		data.put("serviceFee", billInvoice.getBillPaymentInfo().getServiceFee());
		data.put("serviceFeeType", billInvoice.getBillPaymentInfo().getServiceFeeType());
		data.put("sourceOfFundFee", billInvoice.getBillPaymentInfo().getSourceOfFundFee());
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/invoice/{invoiceID}/confirm-otp/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse confirmBillPayOtp(@PathVariable String invoiceID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {
		
		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));
		
		Status invoiceStatus = billPaymentService.confirmBillInvoice(invoiceID, otp, accessTokenID);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("invoiceStatus", invoiceStatus.getStatus());

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
}
