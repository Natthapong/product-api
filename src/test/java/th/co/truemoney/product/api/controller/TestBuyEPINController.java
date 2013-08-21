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
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;

public class TestBuyEPINController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";
	
	private static final String createBuyEpinURL = String.format("/buy/e-pin/draft/verifyAndCreate/%s", fakeAccessToken);
	
	private static final String confirmOTPURL = String.format("/buy/e-pin/confirm/%s/%s", "1111111111", fakeAccessToken);
	
	private static final String checkStatusURL = String.format("/buy/e-pin/%s/status/%s", "1111111111", fakeAccessToken);
	
	@Test
	public void createBuyEpinSuccess() throws Exception {
		
		String amount = "50";
		BigDecimal epinAmount = new BigDecimal(amount);
		OTP otp = new OTP();
		otp.setMobileNumber("0811111111");
		otp.setReferenceCode("xxxx");
		otp.setOtpString("123456");
		
		BuyProduct buyProduct = new BuyProduct("epin_c", epinAmount);
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setRecipientMobileNumber("0811111111");
		
		when(buyProductServiceMock.createAndVerifyBuyProductDraft(anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(buyProductDraft);
		
		when(transactionAuthenServiceMock.requestOTP(anyString(), anyString()))
				.thenReturn(otp);
		
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
				TopUpMobileDraft.Status.OTP_CONFIRMED);

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifySuccess(this.doPUT(confirmOTPURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..status").value("CONFIRMED"));

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
}
