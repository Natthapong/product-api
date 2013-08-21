package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public class TestBuyEPINController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";
	
	private static final String createBuyEpinURL = String.format("/buy/e-pin/draft/verifyAndCreate/%s", fakeAccessToken);
	
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
	
}
