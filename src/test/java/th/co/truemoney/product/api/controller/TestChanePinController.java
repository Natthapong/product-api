package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestChanePinController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String changPinURL = String.format("/profile/change-pin/%s",fakeAccessToken);

	@Test
	public void changePinSuccess() throws Exception {
		when(profileServiceMock.changePin(anyString(), anyString(), anyString()))
			.thenReturn("08xxxxxxxx");

		Map<String,String> reqBody = new HashMap<String,String>();
		reqBody.put("oldPin", "0000");
		reqBody.put("pin", "1111");
		
		this.verifySuccess(doPUT(changPinURL, reqBody));
	} 
	
	@Test
	public void changePinFail() throws Exception {
		when(profileServiceMock.changePin(anyString(), anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400, "50001", "Error Description", "TMN-PRODUCT"));

		Map<String,String> reqBody = new HashMap<String,String>();
		reqBody.put("oldPin", "0000");
		reqBody.put("pin", "1111");
		
		this.verifyFailed(doPUT(changPinURL, reqBody)
				.andExpect(jsonPath("$.code").value("50001"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").value("Invalid mobile no or PIN")));
	} 
	
}
