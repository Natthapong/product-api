package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestLoginController {
	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);
	}

	@After
	public void tierDown() {
		reset(this.tmnProfileServiceMock);
	}
	
	@Test 
	public void loginInputValidationFailed() throws Exception {
		when(
			this.tmnProfileServiceMock.login(
				any(Integer.class), 
				any(Login.class)
			)
		).thenReturn("token-string");
			
		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("customer@truemoney.co.th", "password");
		this.mockMvc.perform(post("/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(login)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void loginSuccess() throws Exception {
		when(
			this.tmnProfileServiceMock.login(
				any(Integer.class), 
				any(Login.class)
			)
		).thenReturn("token-string");
		
		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("wrong@email", "password");
		this.mockMvc.perform(post("/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(login)))
			.andExpect(status().is(HttpServletResponse.SC_BAD_REQUEST));
	}
	
	@Test
	public void loginNotSuccess() throws Exception {
		when(
			this.tmnProfileServiceMock.login(
				any(Integer.class), 
				any(Login.class)
			)
		).thenThrow(
				new ServiceInventoryException("4", "Invalid Username or Password", "umarket")
		);
		
		ObjectMapper mapper = new ObjectMapper();
		LoginBean login = new LoginBean("customer@truemoney.co.th", "password");
		this.mockMvc.perform(post("/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(login)))
			.andExpect(status().is(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}

}
