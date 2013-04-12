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
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
@RequestMapping(value = "/payment")
public class BillPaymentController extends BaseController {

	@Autowired
	public BillPaymentService billPaymentService;

	@RequestMapping(value = "/bill/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillInformation(@PathVariable String barcode, @PathVariable String accessTokenID) {

		BillInfo billPaymentInfo = billPaymentService.getBillInformation(barcode, accessTokenID);

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

	@RequestMapping(value = "/bill/create/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse createBillmentInvoice(
			@RequestParam String accessToken,
			@RequestBody BillInfo billPaymentInfo) {

		Bill billInvoice = this.billPaymentService
				.createBill(billPaymentInfo, accessToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("target", billInvoice.getBillInfo().getTarget());
		data.put("logoURL", billInvoice.getBillInfo().getLogoURL());
		data.put("titleTH", billInvoice.getBillInfo().getTitleTH());
		data.put("titleEN", billInvoice.getBillInfo().getTitleEN());

		data.put("ref1TitleTH", billInvoice.getBillInfo().getRef1TitleTH());
		data.put("ref1TitleEN", billInvoice.getBillInfo().getRef1TitleEN());
		data.put("ref1", billInvoice.getBillInfo().getRef1());

		data.put("ref2TitleTH", billInvoice.getBillInfo().getRef2TitleTH());
		data.put("ref2TitleEN", billInvoice.getBillInfo().getRef2TitleEN());
		data.put("ref2", billInvoice.getBillInfo().getRef2());

		data.put("amount", billInvoice.getBillInfo().getAmount());
		data.put("serviceFee", billInvoice.getBillInfo().getServiceFee());
		data.put("serviceFeeType", billInvoice.getBillInfo().getServiceFee().getFeeType());
		data.put("sourceOfFundFee", billInvoice.getBillInfo().getSourceOfFundFees()[0]);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	@RequestMapping(value = "/bill/{billID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody
	ProductResponse confirmBillPaymentOtp(@PathVariable String billID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		OTP otp = new OTP();
		otp.setOtpString(request.get("otpString"));
		otp.setReferenceCode(request.get("otpRefCode"));
		otp.setMobileNumber(request.get("mobileNumber"));

		Bill.Status invoiceStatus = billPaymentService.confirmBill(billID, otp, accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("invoiceStatus", invoiceStatus.getStatus());
		data.put("billPaymentID", billID);

		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@RequestMapping(value = "/bill/{billPaymentID}/status/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentStatus(@PathVariable String billPaymentID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {
		//TODO
		return null;
	}
	
	@RequestMapping(value = "/bill/{billPaymentID}/detail/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody
	ProductResponse getBillPaymentDetail(@PathVariable String billPaymentID,
			@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {
		//TODO
		return null;
	}

}
