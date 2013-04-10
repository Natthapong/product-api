package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;

public class TestBillPaymentController extends BaseTestController {

	String fakeAccessToken = "111111111111";
	String barcode = "|01234567890";
	String getBillInformationURL = String.format("/bill-payment/barcode/%s/%s", barcode, fakeAccessToken);
	
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
	public void getBillInformationFail() {
		
	}
	
}
