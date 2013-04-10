package th.co.truemoney.product.api.controller;

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
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@Controller
@RequestMapping(value = "/billpay")
public class BillPayController extends BaseController {

	@Autowired
	public BillPaymentService billPaymentService;

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/send-otp/{accessToken}", method = RequestMethod.POST)
	public @ResponseBody
	ProductResponse verifyBill(@PathVariable String draftTransactionID,
			@PathVariable String accessToken,
			@RequestBody BillPaymentInfo billPaymentInfo) {

		BillInvoice billInvoice = this.billPaymentService
				.createBillInvoice(billPaymentInfo, accessToken);
		
		Map<String, Object> data = new HashMap<String, Object>();

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
