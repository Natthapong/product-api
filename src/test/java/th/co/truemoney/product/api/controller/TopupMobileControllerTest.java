package th.co.truemoney.product.api.controller;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.util.ProductResponseFactory;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TopupMobileControllerTest extends BaseTestController {
	
	 private static final String fakeAccessToken = "111111111111";
	 
	 private static final String verifyTopUpMobileURL = String.format("/topup/mobile/draft/verifyAndCreate/%s", fakeAccessToken);
	
	@Autowired
	TopupMobileController controller;
	
	private Map<String, String> request;
	private String accessToken = "accessToken";
	

	@Before
	public void setUp() throws Exception {
		request = new HashMap<String, String>();
	}

	@Test
	public void failToVerifyTopUpWhenMobileIsIncorrectFormat() {
		assertValidationFail("xxxxxxxxxx");
		assertValidationFail("0893333");
		assertValidationFail("09 080 3242");
	}
	
	@Test
	public void verifySuccuss() {
		
		ProductResponse response = null;
		try {
			request.put("mobileNumber", "0894445266");
			response = controller.verifyAndCreate(accessToken, request);
		} catch (InvalidParameterException ex) {
			Assert.fail();
		}
		
		Map<String, Object> data = response.getData();
		Assert.assertEquals("1111111111", data.get("draftTransactionID"));
		Assert.assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png", data.get("logoURL"));
		Assert.assertEquals("0894445266", data.get("mobileNumber"));
		Assert.assertEquals(new BigDecimal(500), data.get("amount"));
		Assert.assertEquals(new BigDecimal(15), data.get("fee"));
		Assert.assertEquals(new BigDecimal(515), data.get("totalAmount"));
	}
	
	@Test
	public void integrationTest() throws Exception {
		  Map<String, String> reqBody = new HashMap<String, String>();
		  reqBody.put("mobileNumber", "0894445266");
		  reqBody.put("amount", "500");
		  
		this.verifySuccess(this.doPOST(verifyTopUpMobileURL, reqBody ))
			.andExpect(jsonPath("data").exists());
	}

	private void assertValidationFail(String mobileNumber) {
		try {
			request.put("mobileNumber", mobileNumber);
			controller.verifyAndCreate(accessToken, request);
			Assert.fail();
		} catch (InvalidParameterException ex) {
		}
	}

}
