package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.http.MediaType;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestLoginController extends BaseTestController {

	@Test
	public void loginInputValidationFailed() throws Exception {
		when(
				this.profileServiceMock.login(any(Integer.class),
						any(Login.class))).thenReturn("token-string");
		TmnProfile tmnProfile = new TmnProfile("Jonh Doe", new BigDecimal(100.00));
		when(this.profileServiceMock.getTruemoneyProfile(any(String.class)))
				.thenReturn(tmnProfile);

		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("customer@truemoney.co.th", "password",
				"email");
		this.mockMvc.perform(
				post("/signin").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(login))).andExpect(
				status().isOk());
	}

	@Test
	public void loginSuccess() throws Exception {
		when(
				this.profileServiceMock.login(any(Integer.class),
						any(Login.class))).thenReturn("token-string");

		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("wrong@email", "password", "email");
		this.mockMvc.perform(
				post("/signin").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(login))).andExpect(
				status().is(HttpServletResponse.SC_BAD_REQUEST));
	}

	@Test
	public void loginNotSuccess() throws Exception {
		when(
				this.profileServiceMock.login(any(Integer.class),
						any(Login.class))).thenThrow(
				new ServiceInventoryException("4",
						"Invalid Username or Password", "umarket"));

		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("customer@truemoney.co.th", "password",
				"email");
		this.mockMvc.perform(
				post("/signin").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(login))).andExpect(
				status().is(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}
}
