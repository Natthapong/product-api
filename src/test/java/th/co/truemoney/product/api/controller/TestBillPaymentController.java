package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestBillPaymentController extends BaseTestController {

	String fakeAccessToken = "111111111111";
	String barcode = "|01234567890";
	String getBillInformationURL = String.format("/bill-payment/barcode/%s/%s", barcode, fakeAccessToken);
	String verifyTransferURL = String.format(
			"/bill-payment/invoice?accessToken=%s", "1111111111111");
	
	@Test
	public void urlCreateBillInvoice() throws Exception {
		BillInvoice billInvoice = new BillInvoice("123",Status.CREATED,new BillPaymentInfo("12345", "6666", "7777", new BigDecimal(5000)));
		billInvoice.setID("9999");
		when(
				this.billPaymentServiceMock.createBillInvoice(
						any(BillPaymentInfo.class), anyString())).thenReturn(
								billInvoice);
		
		this.verifySuccess(this
				.doPOST(verifyTransferURL, new BillPaymentInfo())).andDo(print())
				.andExpect(jsonPath("$.data.amount").value(5000));
	}
	
	@Test
	public void confirmBillPayOtp() throws Exception {
		String confirmOtpUrl = String.format("/bill-payment/invoice/%s/confirm-otp/%s", "transaction_id", "access_token");
		when(this.billPaymentServiceMock.confirmBillInvoice(anyString(), any(OTP.class), anyString())).thenReturn(Status.OTP_CONFIRMED);
		this.verifySuccess(this.doPOST(confirmOtpUrl, new OTP()));
	}
	
	
	
	@Test
	public void getBillInformationSuccess() throws Exception {
		//given 
		BillPaymentInfo stubbedBillPaymentInfo = new BillPaymentInfo();
		
		//when
		when(billPaymentServiceMock.getBillInformation(anyString(), anyString())).thenReturn(stubbedBillPaymentInfo);
		
		//then
		this.verifySuccess(this.doGET(getBillInformationURL));		
	}
	
	@Test
	public void getBillInformationFail() throws Exception {
		//given 
		ServiceInventoryException s = new ServiceInventoryException(400, "", "", "TMN-PRODUCT");
		
		//when
		when(billPaymentServiceMock.getBillInformation(anyString(), anyString())).thenThrow(s);
		
		//then
		this.verifyFailed(this.doGET(getBillInformationURL));	
	}
	
}
