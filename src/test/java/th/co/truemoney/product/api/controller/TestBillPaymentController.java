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
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFundFee;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestBillPaymentController extends BaseTestController {

	String fakeAccessToken = "111111111111";
	String barcode = "|01234567890";
	String getBillInformationURL = String.format("/bill-payment/barcode/%s/%s", barcode, fakeAccessToken);
	String verifyTransferURL = String.format(
			"/bill-payment/invoice?accessToken=%s", "1111111111111");

	@Test
	public void urlCreateBillInvoice() throws Exception {
		BillInvoice billInvoice = new BillInvoice("123",Status.CREATED, createStubbedBillPaymentInfo());
		billInvoice.setID("9999");
		when(
				this.billPaymentServiceMock.createBillInvoice(any(BillPaymentInfo.class), anyString())).thenReturn(
								billInvoice);

		this.verifySuccess(this
				.doPOST(verifyTransferURL, createStubbedBillPaymentInfo())).andDo(print())
				.andExpect(jsonPath("$.data.amount").value(10000.0));
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
		BillPaymentInfo stubbedBillPaymentInfo = createStubbedBillPaymentInfo();

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

	private BillPaymentInfo createStubbedBillPaymentInfo() {
		BillPaymentInfo billPaymentInfo = new BillPaymentInfo();
		billPaymentInfo.setTarget("tcg");
		billPaymentInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
		billPaymentInfo.setTitleTH("ค่าใช้บริการบริษัทในกลุ่มทรู");
		billPaymentInfo.setTitleEN("Convergence Postpay");

		billPaymentInfo.setRef1TitleTH("โทรศัพท์พื้นฐาน");
		billPaymentInfo.setRef1TitleEN("Fix Line");
		billPaymentInfo.setRef1("010004552");

		billPaymentInfo.setRef2TitleTH("รหัสลูกค้า");
		billPaymentInfo.setRef2TitleEN("Customer ID");
		billPaymentInfo.setRef2("010520120200015601");

		billPaymentInfo.setAmount(new BigDecimal("10000"));

		ServiceFee serviceFee = new ServiceFee();
		serviceFee.setFee(new BigDecimal("1000"));
		serviceFee.setFeeType("THB");
		serviceFee.setTotalFee(new BigDecimal("1000"));
		serviceFee.setMinFeeAmount(new BigDecimal("100"));
		serviceFee.setMaxFeeAmount(new BigDecimal("2500"));
		billPaymentInfo.setServiceFee(serviceFee);

		SourceOfFundFee[] sourceOfFundFees = new SourceOfFundFee[1];
		SourceOfFundFee sourceOfFundFee = new SourceOfFundFee();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFee(new BigDecimal("1000"));
		sourceOfFundFee.setFeeType("THB");
		sourceOfFundFee.setTotalFee(new BigDecimal("1000"));
		sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
		sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
		sourceOfFundFees[0] = sourceOfFundFee;
		billPaymentInfo.setSourceOfFundFees(sourceOfFundFees);

		return billPaymentInfo;
	}

}
