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

}
