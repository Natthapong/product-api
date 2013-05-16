package th.co.truemoney.product.api.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;


public class TestBillPaymentControllerUnit {
	
	BillPaymentController billPaymentController;
	
	BillPaymentService billPaymentServiceMock;
	
	TransactionAuthenService transactionAuthenServiceMock;
	
	TmnProfileService profileServiceMock;
	
	ProductResponseFactory responseFactory;
	
	private static final String fakeAccessTokenID = "111111";
	

	@Before
	public void setup(){
		this.billPaymentController = new BillPaymentController();
		this.billPaymentServiceMock = mock(BillPaymentService.class);
		this.responseFactory = new ProductResponseFactory();
		this.responseFactory.setMessageManager(mock(MessageManager.class));
		this.billPaymentController.setBillPaymentService(billPaymentServiceMock);
		this.billPaymentController.setResponseFactory(responseFactory);
		this.transactionAuthenServiceMock = mock(TransactionAuthenService.class);
		this.billPaymentController.setAuthService(transactionAuthenServiceMock);
		this.profileServiceMock = mock(TmnProfileService.class);
		this.billPaymentController.setProfileService(profileServiceMock);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void favoriteBillPayment() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "tcg");
		request.put("ref1", "010004552");
		request.put("amount", "10000");
		
		when(this.billPaymentServiceMock.retrieveBillInformationWithBillCode(
				anyString(), anyString(), any(BigDecimal.class), anyString()))
		.thenReturn(createStubbedBillInfo());
        
        when(
                billPaymentServiceMock.verifyPaymentAbility(
                        anyString(),
                        any(BigDecimal.class),
                        anyString()
                )
        ).thenReturn(createBillPaymentDraftStubbed());
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		assertEquals( "tcg", data.get("target"));
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png", data.get("logoURL"));
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
		
		List<Map<String, Object>> sofList = (List<Map<String, Object>>) data.get("sourceOfFundFee");
		Map<String, Object> ew = sofList.get(0);
		assertNotNull(ew);
		assertEquals("EW", ew.get("sourceType"));
		assertEquals("THB", ew.get("sourceFeeType"));
		assertEquals(new BigDecimal(1000), ew.get("sourceFee"));
		assertEquals(new BigDecimal(100), ew.get("minSourceFeeAmount"));
		assertEquals(new BigDecimal(2500), ew.get("maxSourceFeeAmount"));
		
		assertNotNull(data.containsKey("billPaymentID"));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void getFavoriteBillInfoFailBillCodeNull() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", null);
		request.put("ref1", "010004552");
		request.put("amount", "10000");
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		assertEquals("50010", resp.getCode());
	}
	
	@Test(expected = InvalidParameterException.class)
	public void getFavoriteBillInfoFailBillCodeEmpty() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "");
		request.put("ref1", "010004552");
		request.put("amount", "10000");
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		assertEquals("50010", resp.getCode());
	}
	
	@Test(expected = InvalidParameterException.class)
	public void getFavoriteBillInfoFailRef1Empty() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "tcg");
		request.put("ref1", "");
		request.put("amount", "10000");
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		assertEquals("50010", resp.getCode());
	}
	
	@Test(expected = InvalidParameterException.class)
	public void getFavoriteBillInfoFailRef1Null() {
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "tcg");
		request.put("ref1", null);
		request.put("amount", "10000");
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		assertEquals("50010", resp.getCode());
	}
	
	@Test
	public void verifyFavoriteBillSuccess(){
		Map<String, String> request = new HashMap<String, String>();
		request.put("billCode", "tcg");
		request.put("ref1", "010004552");
		request.put("amount", "10000");
		
		when(this.billPaymentServiceMock.retrieveBillInformationWithBillCode(
				anyString(), anyString(), any(BigDecimal.class), anyString()))
		.thenReturn(createStubbedBillInfo());
        
        when(
                billPaymentServiceMock.verifyPaymentAbility(
                        anyString(),
                        any(BigDecimal.class),
                        anyString()
                )
        ).thenReturn(createBillPaymentDraftStubbed());
		
		ProductResponse resp = billPaymentController.verifyAndGetBillPaymentFavoriteInfo(fakeAccessTokenID, request);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		assertEquals("OTP_CONFIRMED", String.valueOf(data.get("billPaymentStatus")));
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
		
		verify(
			this.billPaymentServiceMock, times(1)
		).performPayment(anyString(), anyString());
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
		
		verify(
			this.billPaymentServiceMock, times(1)
		).performPayment(anyString(), anyString());
	}
	
	@Test
	public void getBillInformationSuccess(){
		
		when(
                billPaymentServiceMock.retrieveBillInformationWithBarcode(
                        anyString(),
                        anyString()
                )
        ).thenReturn(createStubbedBillInfo());
		
		ProductResponse resp = billPaymentController.getBillInformation("1111111111", fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		assertEquals("tcg", data.get("target"));
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png", data.get("logoURL"));
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
		
		List<Map<String, Object>> sofList = (List<Map<String, Object>>) data.get("sourceOfFundFee");
		Map<String, Object> ew = sofList.get(0);
		assertNotNull(ew);
		assertEquals("EW", ew.get("sourceType"));
		assertEquals("THB", ew.get("sourceFeeType"));
		assertEquals(new BigDecimal(1000), ew.get("sourceFee"));
		assertEquals(new BigDecimal(100), ew.get("minSourceFeeAmount"));
		assertEquals(new BigDecimal(2500), ew.get("maxSourceFeeAmount"));
		
		assertNotNull(data.containsKey("billPaymentID"));
	}
	
	@Test
	public void createBillPaymentSuccess(){
		Map<String, String> request = new HashMap<String, String>();
		request.put("billID", "1111111111");
		request.put("amount", "10000");
		
		Bill billInfo = new Bill();
		billInfo.setAmount(BigDecimal.TEN);
		billInfo.setServiceFee(new ServiceFeeInfo("THB", BigDecimal.ONE));

		BillPaymentDraft bill = new BillPaymentDraft();
		bill.setAmount(BigDecimal.TEN);
        bill.setBillInfo(billInfo);

        when(
                billPaymentServiceMock.verifyPaymentAbility(
                        anyString(),
                        any(BigDecimal.class),
                        anyString()
                )
        ).thenReturn(bill);
        
        when(
                transactionAuthenServiceMock.requestOTP(
                        anyString(),
                        anyString()
                )
        ).thenReturn(new OTP());
		
		ProductResponse resp = billPaymentController.createBillPayment(fakeAccessTokenID, request);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		
		assertTrue(data.containsKey("otpRefCode"));
		assertTrue(data.containsKey("mobileNumber"));
		assertTrue(data.containsKey("billID"));
		assertEquals("11", String.valueOf(data.get("totalAmount")));
	}
	
	 @Test
     public void confirmBillPaymentSuccess() throws Exception {
		 Map<String, String> request = new HashMap<String, String>();
			request.put("otpString", "123456");
			request.put("otpRefCode", "qwer");
			request.put("mobileNumber", "0891234567");
			
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
 		
 		ProductResponse resp = billPaymentController.confirmBillPayment("111111", fakeAccessTokenID, request);
		Map<String, Object> data = resp.getData();
		assertNotNull(data);
		
		assertEquals("CONFIRMED", data.get("billPaymentStatus"));
		assertTrue(data.containsKey("billPaymentID"));
	 }
	 
	 @Test
     public void getBillPaymentStatusSuccess() throws Exception {
             when(
                     billPaymentServiceMock.getBillPaymentStatus(
                             anyString(),
                             anyString()
                     )
             ).thenReturn(BillPaymentTransaction.Status.PROCESSING);
             
            ProductResponse resp = billPaymentController.getBillPaymentStatus("11111", fakeAccessTokenID);
     		Map<String, Object> data = resp.getData();
     		assertNotNull(data);
     		
     		assertEquals("PROCESSING", data.get("billPaymentStatus"));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
     }
	 
	 @Test
     public void getBillPaymentDetailSuccess() throws Exception {
		 Map<String, String> request = new HashMap<String, String>();
			request.put("billCode", "tcg");
			request.put("ref1", "010004552");
			request.put("amount", "10000");
	 
         Bill billInfo = createStubbedBillInfo();

         BillPaymentDraft bill = new BillPaymentDraft();
         bill.setBillInfo(billInfo);
         bill.setAmount(new BigDecimal("10000"));

         BillPaymentTransaction bpay = new BillPaymentTransaction();
         bpay.setConfirmationInfo(new BillPaymentConfirmationInfo());
         bpay.setDraftTransaction(bill);

         when(
                 billPaymentServiceMock.getBillPaymentResult(
                         anyString(),
                         anyString()
                 )
         ).thenReturn(bpay);
         
         when(
         		profileServiceMock.getEwalletBalance(
         				anyString()
         		)
         ).thenReturn(new BigDecimal(100.00));

        ProductResponse resp = billPaymentController.getBillPaymentDetail("111111", fakeAccessTokenID);
 		Map<String, Object> data = resp.getData();
 		assertNotNull(data);
 		
 		assertEquals( "tcg", data.get("target"));
 		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png", data.get("logoURL"));
 		assertEquals("", data.get("titleTh"));
 		assertEquals("", data.get("titleEn"));
 		
 		assertEquals("โทรศัพท์พื้นฐาน", data.get("ref1TitleTh"));
 		assertEquals("Fix Line", data.get("ref1TitleEn"));
 		assertEquals("010004552", data.get("ref1"));
 		
 		assertEquals("รหัสลูกค้า", data.get("ref2TitleTh"));
 		assertEquals("Customer ID", data.get("ref2TitleEn"));
 		assertEquals("010520120200015601", data.get("ref2"));
 		
 		assertEquals(new BigDecimal(10000), data.get("amount"));
 		assertEquals(new BigDecimal(12000), data.get("totalAmount"));
 		assertEquals(new BigDecimal(2000), data.get("totalFee"));
 		
 		assertEquals("Wallet", data.get("sourceOfFund"));
 		
 		assertTrue(data.containsKey("transactionDate"));
 		assertTrue(data.containsKey("transactionID"));
 		
 		assertFalse(data.containsKey("remarkEn"));
 		assertFalse(data.containsKey("remarkTh"));
             
 		assertEquals(new BigDecimal(100), data.get("currentEwalletBalance"));
 		assertEquals("false", data.get("isFavoritable"));
 		assertEquals("false", data.get("isFavorited"));
 		
     }
	
	private Bill createStubbedBillInfo() {
        Bill billInfo = new Bill();
        billInfo.setID("111111");
        billInfo.setTarget("tcg");
        billInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png");
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
        billInfo.setFavorited(false);
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
	
	private BillPaymentDraft createBillPaymentDraftStubbed(){
		Bill bill = createStubbedBillInfo();
		return new BillPaymentDraft("1111111111", bill, new BigDecimal(11000), "123567890", Status.OTP_CONFIRMED);
	}

}
