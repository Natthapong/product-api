package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public class TestBillPaymentControllerUnit extends BaseTestController{
	
	@Autowired
	BillPaymentController billPaymentController;
	
	private static final String fakeAccessTokenID = "111111";
	
	@SuppressWarnings("unchecked")
	@Test
	public void favoriteBillPayment() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "tcg");
		request.put("ref1", "010004552");
		request.put("amount", "10000");
		
		when(this.billPaymentServiceMock.retrieveBillInformationWithBillCode(anyString(), anyString(), any(BigDecimal.class), anyString()))
		.thenReturn(createStubbedBillInfo());
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		assertEquals( "tcg", data.get("billCode"));
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png", data.get("logoURL"));
		assertEquals("", data.get("titleTh"));
		assertEquals("", data.get("titleEn"));
		
		assertEquals("โทรศัพท์พื้นฐาน", data.get("ref1TitleTh"));
		assertEquals("Fix Line", data.get("ref1TitleEn"));
		assertEquals("010004552", data.get("ref1"));
		
		assertEquals("รหัสลูกค้า", data.get("ref2TitleTh"));
		assertEquals("Customer ID", data.get("ref2TitleEn"));
		assertEquals("010520120200015601", data.get("ref2"));
		
		assertEquals(new BigDecimal("10000.00"), data.get("amount"));
		assertEquals(new BigDecimal(1000), data.get("serviceFee"));
		assertEquals("Y", data.get("partialPaymentAllow"));
		
		assertEquals(new BigDecimal(10), data.get("minAmount"));
		assertEquals(new BigDecimal(5000), data.get("maxAmount"));
		
		assertEquals("THB", data.get("serviceFeeType"));
		assertEquals(new BigDecimal(1000), data.get("serviceFee"));
		
		List<JSONObject> sourceOfFundFeeList = (List<JSONObject>) data.get("sourceOfFundFee");
		assertEquals(1,sourceOfFundFeeList.size());
		JSONObject sourceOfFundFee = sourceOfFundFeeList.get(0);
		assertEquals(new BigDecimal(1000), sourceOfFundFee.get("sourceFee"));
		assertEquals("THB", sourceOfFundFee.get("sourceFeeType"));
		assertEquals(new BigDecimal(100), sourceOfFundFee.get("minSourceFeeAmount"));
		assertEquals(new BigDecimal(2500), sourceOfFundFee.get("maxSourceFeeAmount"));
		assertEquals("EW", sourceOfFundFee.get("sourceType"));
		
		assertNotNull(data.containsKey("billPaymentID"));
	}
	
	@Test
	public void confirmBillPaySkipOTPChecking() throws Exception {
		BillPaymentDraft otpConfirmedDraft = new BillPaymentDraft(null, null, null, null, BillPaymentDraft.Status.OTP_CONFIRMED);
		when(
			this.billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString())
		).thenReturn(otpConfirmedDraft);
				
		when(
			this.billPaymentServiceMock.performPayment(anyString(), anyString())
		).thenReturn(BillPaymentTransaction.Status.PROCESSING);
		
		billPaymentController.confirmBillPayment("", "", new HashMap<String, String>());
		
		verify(
			this.transactionAuthenServiceMock, never()
		).verifyOTP(anyString(), any(OTP.class), anyString());
	}
	
	@Test
	public void confirmBillPayWithOTPChecking() throws Exception {
		BillPaymentDraft otpSentDraft = new BillPaymentDraft(null, null, null, null, BillPaymentDraft.Status.OTP_SENT);
		when(
			this.billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString())
		).thenReturn(otpSentDraft);
				
		when(
			this.transactionAuthenServiceMock.verifyOTP(anyString(), any(OTP.class), anyString())
		).thenReturn(DraftTransaction.Status.OTP_CONFIRMED);
		
		when(
			this.billPaymentServiceMock.performPayment(anyString(), anyString())
		).thenReturn(BillPaymentTransaction.Status.PROCESSING);
		
		billPaymentController.confirmBillPayment("", "", new HashMap<String, String>());
		
		verify(
			this.transactionAuthenServiceMock, times(1)
		).verifyOTP(anyString(), any(OTP.class), anyString());
		
	}
	
	private Bill createStubbedBillInfo() {
        Bill billInfo = new Bill();
        billInfo.setTarget("tcg");
        billInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
        billInfo.setTitleTH("ค่าใช้บริการบริษัทในกลุ่มทรู");
        billInfo.setTitleEN("Convergence Postpay");

        billInfo.setRef1TitleTH("โทรศัพท์พื้นฐาน");
        billInfo.setRef1TitleEN("Fix Line");
        billInfo.setRef1("010004552");

        billInfo.setRef2TitleTH("รหัสลูกค้า");
        billInfo.setRef2TitleEN("Customer ID");
        billInfo.setRef2("010520120200015601");

        billInfo.setAmount(new BigDecimal("10000"));

        billInfo.setFavoritable(false);
        billInfo.setPartialPayment("Y");
        billInfo.setMinAmount(new BigDecimal(10));
        billInfo.setMaxAmount(new BigDecimal(5000));

        ServiceFeeInfo serviceFee = new ServiceFeeInfo();
        serviceFee.setFeeRate(new BigDecimal("1000"));
        serviceFee.setFeeRateType("THB");
        billInfo.setServiceFee(serviceFee);

        SourceOfFund[] sourceOfFundFees = new SourceOfFund[1];
        SourceOfFund sourceOfFundFee = new SourceOfFund();
        sourceOfFundFee.setSourceType("EW");
        sourceOfFundFee.setFeeRate(new BigDecimal("1000"));
        sourceOfFundFee.setFeeRateType("THB");
        sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
        sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
        sourceOfFundFees[0] = sourceOfFundFee;
        billInfo.setSourceOfFundFees(sourceOfFundFees);

        return billInfo;
}


}
