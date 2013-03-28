package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.Test;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestLoginController extends BaseTestController {
	
	@Test
	public void loginSuccess() throws Exception {
		when(
			this.profileServiceMock.login(
				any(Integer.class),
				any(Login.class)
			)
		).thenReturn("token-string");
		
		when(
			this.profileServiceMock.getTruemoneyProfile(
				any(String.class)
			)
		).thenReturn(new TmnProfile("Jonh Doe", new BigDecimal(100.00)));

		this.verifySuccess(
				this.doPOST("/signin", 
						new LoginBean("customer@truemoney.co.th", "password", "email")));
	}

	@Test
	public void loginInputValidationFailed() throws Exception {
		when(
			this.profileServiceMock.login(
				any(Integer.class),
				any(Login.class)
			)
		).thenReturn("token-string");
		
		this.verifyBadRequest(
				this.doPOST("/signin", 
						new LoginBean("wrong@email", "password", "email")));
	}

	@Test
	public void loginNotSuccess() throws Exception {
		when(
			this.profileServiceMock.login(
				any(Integer.class),
				any(Login.class)
			)
		).thenThrow(
				new ServiceInventoryException("4", "Invalid Username or Password", "umarket"));
		this.verifyFailed(
				this.doPOST("/signin", 
						new LoginBean("customer@truemoney.co.th", "password","email"))
		).andExpect(jsonPath("$.code").value("4")
		).andExpect(jsonPath("$.messageEn").value("Invalid Email or Password")
		).andExpect(jsonPath("$.namespace").value("umarket"));
	}
	
	@Test
	public void accountNotActive() throws Exception {
		when (
			this.profileServiceMock.login(
				any(Integer.class), 
				any(Login.class)
			)
		).thenThrow(
				new ServiceInventoryException("1006", "confirm umarket failed.", "TMN-SERVICE-INVENTORY")
		);
		this.verifyFailed(
				this.doPOST("/signin", 
						new LoginBean("customer@truemoney.co.th", "password", "email"))
		).andExpect(jsonPath("$.code").value("1006")
		).andExpect(jsonPath("$.messageEn").value("confirm umarket failed.")
		).andExpect(jsonPath("$.namespace").value("TMN-SERVICE-INVENTORY")
		).andExpect(jsonPath("$.messageTh").value(containsString("02-647-3333")));
	}
}
