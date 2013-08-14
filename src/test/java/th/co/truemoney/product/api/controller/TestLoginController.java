package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.Test;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestLoginController extends BaseTestController {

	@Test
	public void loginSuccess() throws Exception {
		when(
			this.profileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class))
		).thenReturn("token-string");

		when(
			this.profileServiceMock.getTruemoneyProfile(
				any(String.class)
			)
		).thenReturn(new TmnProfile("Jonh Doe", new BigDecimal(100.00)));

		this.verifySuccess(
				this.doPOST("/signin",
						new LoginBean("customer@truemoney.co.th", "password", "email")))
						.andExpect(jsonPath("$.data").exists())
						.andExpect(jsonPath("$..email").exists())
						.andExpect(jsonPath("$..mobileNumber").exists())
						.andExpect(jsonPath("$..currentBalance").exists())
						.andExpect(jsonPath("$..fullname").exists())
						.andExpect(jsonPath("$..accessToken").exists())
						.andExpect(jsonPath("$..imageURL").exists());
	}

	@Test
	public void loginInputValidationFailed() throws Exception {
		when(
			this.profileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class))
		).thenReturn("token-string");

		this.verifyBadRequest(
				this.doPOST("/signin",
						new LoginBean("wrong@email", "password", "email")));
	}

	@Test
	public void loginNotSuccess() throws Exception {
		when(
			this.profileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class)			)
		).thenThrow(
				new ServiceInventoryException(400, "4", "Invalid Username or Password", "core"));
		this.verifyFailed(
				this.doPOST("/signin",
						new LoginBean("customer@truemoney.co.th", "password","email"))
		).andExpect(jsonPath("$.code").value("4")
		).andExpect(jsonPath("$.messageEn").value("Invalid Email or Password")
		).andExpect(jsonPath("$.namespace").value("core"));
	}

	@Test
	public void accountNotActive() throws Exception {
		when (
			this.profileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class))
		).thenThrow(
				new ServiceInventoryException(400, "1006", "confirm umarket failed.", "TMN-SERVICE-INVENTORY")
		);
		this.verifyFailed(
				this.doPOST("/signin",
						new LoginBean("customer@truemoney.co.th", "password", "email"))
		).andExpect(jsonPath("$.code").value("1006")
		).andExpect(jsonPath("$.messageEn").value("confirm umarket failed.")
		).andExpect(jsonPath("$.namespace").value("TMN-SERVICE-INVENTORY")
		).andExpect(jsonPath("$.messageTh").value(containsString("02-647-3333")));
	}
	
	@Test
	public void getUserprofileSuccess() throws Exception{
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setBalance(new BigDecimal(10000));
		tmnProfile.setEmail("apinya@truecorp.co.th");
		tmnProfile.setFullname("Apinya Ukachoke");
		tmnProfile.setMobileNumber("0812345678");
		tmnProfile.setHasPassword(Boolean.TRUE);
		tmnProfile.setHasPin(Boolean.FALSE);
		tmnProfile.setImageFileName("xxx.jsp");
		
		when( this.profileServiceMock.getTruemoneyProfile(anyString())).thenReturn(tmnProfile);
		
		this.verifySuccess(this.doGET("/profile/1111111111"))
		.andExpect(jsonPath("$..email").value("apinya@truecorp.co.th"))
		.andExpect(jsonPath("$..fullname").value("Apinya Ukachoke"))
		.andExpect(jsonPath("$..mobileNumber").value("0812345678"))
		.andExpect(jsonPath("$..currentBalance").value("10000"))
		.andExpect(jsonPath("$..hasPassword").value(Boolean.TRUE))
		.andExpect(jsonPath("$..hasPin").value(Boolean.FALSE))
		.andExpect(jsonPath("$..imageURL").exists());
		
	}
}
