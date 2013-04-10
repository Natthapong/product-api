package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public class TestBillController extends BaseTestController {

	String verifyTransferURL = String.format(
			"/bill-payment/invoice/%s/send-otp/%s", "1111111111111",
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

	
	@Test
	public void confirmBillPayOtp() throws Exception {
		String confirmOtpUrl = String.format("/bill-payment/invoice/%s/confirm-otp/%s", "transaction_id", "access_token");
		when(this.billPaymentServiceMock.confirmBillInvoice(anyString(), any(OTP.class), anyString())).thenReturn(Status.OTP_CONFIRMED);
		this.verifySuccess(this.doPOST(confirmOtpUrl, new OTP()));
	}

}
