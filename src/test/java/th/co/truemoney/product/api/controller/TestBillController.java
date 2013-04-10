package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;

public class TestBillController extends BaseTestController {

	String verifyTransferURL = String.format(
			"/billpay/draft-transaction/%s/send-otp/%s", "1111111111111",
			"1111111111111");

	@Test
	public void urlCreateBillInvoice() throws Exception {
		when(
				this.billPaymentServiceMock.createBillInvoice(
						any(BillPaymentInfo.class), anyString())).thenReturn(
				new BillInvoice());
		this.verifySuccess(this
				.doPOST(verifyTransferURL, new BillPaymentInfo()));
	}

}
