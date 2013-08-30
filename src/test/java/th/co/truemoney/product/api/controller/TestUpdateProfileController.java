package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestUpdateProfileController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String updateProfileURL = String.format("/profile/%s",fakeAccessToken);

	@Test
	public void changeFullnameSuccess() throws Exception {
		when(profileServiceMock.changeFullname(anyString(), anyString()))
			.thenReturn(new TmnProfile("Jonh Doe", new BigDecimal(100.00)));

		Map<String,String> reqBody = new HashMap<String,String>();
		reqBody.put("fullname", "new-fullname");
		
		this.verifySuccess(doPUT(updateProfileURL, reqBody)
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..email").exists())
				.andExpect(jsonPath("$..mobileNumber").exists())
				.andExpect(jsonPath("$..currentBalance").exists())
				.andExpect(jsonPath("$..fullname").exists()));
	} 
	
	@Test
	public void changeFullnameFail() throws Exception {
		when(profileServiceMock.changeFullname(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"","",""));

		Map<String,String> reqBody = new HashMap<String,String>();
		reqBody.put("fullname", "new-fullname");
		
		this.verifyFailed(doPUT(updateProfileURL, reqBody));
	} 
	
	@Test	
	public void changeProfileImageStatusSuccess() throws Exception {
		TmnProfile profile = new TmnProfile();
		profile.setProfileImageStatus(true);
		when(
			profileServiceMock.changeProfileImageStatus(anyString(), any(Boolean.class))
		).thenReturn(profile);
		
		Map<String,String> reqBody = new HashMap<String,String>();
		reqBody.put("status", "true");
		
		this.verifySuccess(doPOST("/profile/change-image-status/" + fakeAccessToken, reqBody)
				.andExpect(jsonPath("$..profileImageStatus").value(true)));
	}
}
