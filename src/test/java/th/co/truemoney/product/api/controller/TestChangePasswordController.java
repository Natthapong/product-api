package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestChangePasswordController extends BaseTestController {
	
	@Test
	public void changePasswordSuccess()  throws Exception {
		
		//given
		Map<String, String> data = new HashMap<String, String>();
		data.put("oldPassword", "xxxxxxxxx");
		data.put("password", "yyyyyyyyy");
		
		String stubbedEmail = "success@change.password";
		when(this.profileServiceMock.changePassword(any(String.class), anyString(), anyString())).thenReturn(stubbedEmail);
		
		//when //then
		this.verifySuccess(this.doPUT("/profile/change-password/111111111111", data))
			.andExpect(jsonPath("$..email").value("success@change.password"));;
		
	}
}
