package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductConfirmationInfo;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestBuyEPINController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";
	
	private static final String createBuyEpinURL = String.format("/buy/e-pin/draft/verifyAndCreate/%s", fakeAccessToken);
	
	private static final String confirmOTPURL = String.format("/buy/e-pin/confirm/%s/%s", "1111111111", fakeAccessToken);
	
	private static final String checkStatusURL = String.format("/buy/e-pin/%s/status/%s", "1111111111", fakeAccessToken);
	
	private static final String getBuyEpinURL = String.format("/buy/e-pin/%s/details/%s", "1111111111", fakeAccessToken);
	
	private static final String resendOtpURL = String.format("/buy/e-pin/resend-otp/%s/%s", "1111111111", fakeAccessToken);
	
	private static final String resendEpinURL = String.format("/buy/e-pin/resend-pin/%s/%s", "222222", fakeAccessToken);
	
	@Test
	public void createBuyEpinSuccess() throws Exception {
		
		String amount = "50";
		BigDecimal epinAmount = new BigDecimal(amount);
		
		BuyProduct buyProduct = new BuyProduct("epin_c", epinAmount);
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setRecipientMobileNumber("0811111111");
		
		when(buyProductServiceMock.createAndVerifyBuyProductDraft(anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(createBuyProductDraftStub());
		
		when(transactionAuthenServiceMock.requestOTP(anyString(), anyString()))
				.thenReturn(createOtpStub());
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", amount);
        params.put("recipientMobileNumber", "0811111111");
        
        this.verifySuccess(this.doPOST(createBuyEpinURL, params));
        
	}
	
	@Test
	public void validateEpinPriceFail() throws Exception {
		
		String amount = "301";
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", amount);
        params.put("recipientMobileNumber", "0811111111");
        this.verifyBadRequest(this.doPOST(createBuyEpinURL, params));
		
	}
	
	@Test
	public void integrationTestConfirmOTPSuccess() throws Exception {

		when(
				transactionAuthenServiceMock.verifyOTP(anyString(),
						any(OTP.class), anyString())).thenReturn(
				BuyProductDraft.Status.OTP_CONFIRMED);

		when(buyProductServiceMock.performBuyProduct(anyString(),anyString()))
			.thenReturn(BuyProductTransaction.Status.PROCESSING);
		
		
		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");
		reqBody.put("mobileNumber", "08xxxxxxxx");

		this.verifySuccess(this.doPUT(confirmOTPURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..status").value("PROCESSING"));

		Mockito.verify(transactionAuthenServiceMock).verifyOTP(anyString(),	any(OTP.class), anyString());
		Mockito.verify(buyProductServiceMock).performBuyProduct(anyString(), anyString());
	}
	
	@Test
	public void confirmOTPFail() throws Exception {

		when(
				transactionAuthenServiceMock.verifyOTP(anyString(),
						any(OTP.class), anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifyFailed(this.doPUT(confirmOTPURL, reqBody));

		Mockito.verify(buyProductServiceMock, Mockito.never()).performBuyProduct(anyString(), anyString());

	}
	
	@Test
	public void integrationTestCheckStatusSuccess() throws Exception {
		
		when(
				buyProductServiceMock.getBuyProductStatus(anyString(),
						anyString())).thenReturn(
				BuyProductTransaction.Status.SUCCESS);

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifySuccess(this.doGET(checkStatusURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..status").value("SUCCESS"));
	}

	@Test
	public void checkStatusFail() throws Exception {

		when(
				buyProductServiceMock.getBuyProductStatus(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifyFailed(this.doGET(checkStatusURL, reqBody));

	}
	
	@Test
	public void integrationTestGetBuyEpinDetailsSuccess() throws Exception {

		when(
				buyProductServiceMock.getBuyProductResult(anyString(),
						anyString())).thenReturn(
								createBuyProductTransactionStub());

		when(profileServiceMock.getEwalletBalance(anyString())).thenReturn(
				new BigDecimal(5000));

		this.verifySuccess(this.doGET(getBuyEpinURL)).andExpect(
				jsonPath("data").exists());
	}
	
	@Test
	public void getBuyEpinDetailsFail() throws Exception {

		when(
				buyProductServiceMock.getBuyProductResult(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getBuyEpinURL));
	}
	
	@Test
	public void resendOTPSuccess() throws Exception {
		when(transactionAuthenServiceMock.requestOTP(anyString(), anyString())).thenReturn(createOtpStub());
		when(buyProductServiceMock.getBuyProductDraftDetails(anyString(), anyString())).thenReturn(createBuyProductDraftStub());
		
		
		this.verifySuccess(this.doGET(resendOtpURL)).andExpect(
				jsonPath("data").exists());
	}
	
	@Test
	public void resendPINSuccess() throws Exception {
		when(activityServiceMock.resendEPIN(any(Long.class), anyString())).thenReturn(Boolean.TRUE);
		
		this.verifySuccess(this.doGET(resendEpinURL)).andExpect(jsonPath("data").exists());
	}
	
	private BuyProductDraft createBuyProductDraftStub() {
		BuyProduct buyProduct = new BuyProduct("epin_c", new BigDecimal(50));
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setBuyProductInfo(buyProduct);
		return buyProductDraft;
	}
	
	private BuyProductTransaction createBuyProductTransactionStub() {

		BuyProductTransaction transaction = new BuyProductTransaction();
		transaction.setType("type");
		transaction.setStatus(Status.SUCCESS);
		transaction.setID("1111111111");
		
		BuyProductConfirmationInfo buyProductInfo = new BuyProductConfirmationInfo();
		buyProductInfo.setTransactionID("1111111111");
		buyProductInfo.setTransactionDate("25/04/13 10:03");
		buyProductInfo.setPin("12345678901234");
		buyProductInfo.setSerial("123456789012345678");
		buyProductInfo.setExpireDate("25/04/13");
		transaction.setConfirmationInfo(buyProductInfo);
		
		BuyProductDraft draft = new BuyProductDraft();
		draft.setTransactionID("1111111111");
		draft.setRecipientMobileNumber("0891111111");
		draft.setSelectedSourceOfFundType("EW");
		
		BuyProduct buyProduct = new BuyProduct();
		buyProduct.setTarget("epin_c");
		buyProduct.setAmount(new BigDecimal(50));
		buyProduct.setID("1111111111");
		
		draft.setBuyProductInfo(buyProduct);
		
		draft.setType("type");
		draft.setOtpReferenceCode("ASDF");
		draft.setAccessTokenID(fakeAccessToken);
		draft.setID("1111111111");
		draft.setStatus(th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status.OTP_CONFIRMED);		
		
		transaction.setDraftTransaction(draft);
		
		return transaction;
	}
	
	private OTP createOtpStub() {
		OTP otp = new OTP();
		otp.setMobileNumber("0811111111");
		otp.setReferenceCode("xxxx");
		otp.setOtpString("123456");
		return otp;
	}
}
