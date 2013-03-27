package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestRegisterController {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;

	private String validateEmailURL = "/ewallet/profiles/validate-email";
	private String createProfileURL = "/ewallet/profiles";
	private String confirmCreateProfileURL = "/ewallet/profiles/verify-otp";

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
	public void validateEmailSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String email = "apinya@gmail.com";

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email","apinya@gmail.com");

		when(
				this.tmnProfileServiceMock.validateEmail(anyInt(), any(String.class)) )
			.thenReturn(email);

			this.mockMvc
					.perform(post(validateEmailURL).content(mapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andExpect(jsonPath("$..email").value("apinya@gmail.com"))
					.andDo(print());
	}

	@Test
	public void validateEmailInvalidFormat() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String failedCode = "40000";
		String failedMessage = "Invalid email format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("email","apinya@gmail.com");

		when(
				this.tmnProfileServiceMock.validateEmail(anyInt(), any(String.class)) )
			.thenThrow(new ServiceInventoryException(failedCode, failedMessage,
					failedNamespace));

			this.mockMvc
					.perform(post(validateEmailURL).content(mapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andDo(print());
	}

	@Test
	public void createProfileSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> tmnProfile = new HashMap<String, Object>();
		tmnProfile.put("email","apinya@gmail.com");
		tmnProfile.put("password","werw2345");
		tmnProfile.put("mobileNumber","0899999999");
		tmnProfile.put("fullname","Apinya Ukachoke");
		
		OTP otp = new OTP();
		otp.setMobileNo("0899999999");
		otp.setOtpString("123456");
		otp.setReferenceCode("wert");

		when(
				this.tmnProfileServiceMock.createProfile(anyInt(), any(TmnProfile.class)) )
			.thenReturn(otp);

			this.mockMvc
					.perform(post(createProfileURL).content(mapper.writeValueAsBytes(tmnProfile)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andExpect(jsonPath("$..otpRefCode").value("wert"))
					.andDo(print());
	}

	@Test
	public void createProfileFail() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String failedCode = "50001";
		String failedMessage = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String, Object> tmnProfile = new HashMap<String, Object>();
		tmnProfile.put("email","apinya@gmail.com");
		tmnProfile.put("password","werw2345");
		tmnProfile.put("mobileNumber","0899999999");
		tmnProfile.put("fullname","Apinya Ukachoke");

		when(
				this.tmnProfileServiceMock.createProfile(anyInt(), any(TmnProfile.class)) )
			.thenThrow(new ServiceInventoryException(failedCode, failedMessage,
					failedNamespace));

			this.mockMvc
					.perform(post(createProfileURL).content(mapper.writeValueAsBytes(tmnProfile)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andDo(print());
	}

	@Test
	public void confirmCreateProfileSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileno("0899999999");
		profileMock.setPassword("werw2345");
		profileMock.setBalance(new BigDecimal(0.00));
		profileMock.setStatus(0);
		profileMock.setType("C");
		profileMock.setThaiID("1212121212121");

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "qwer");
		mockData.put("checksum", "111111111111");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenReturn(profileMock);

		when(
				this.tmnProfileServiceMock.login(any(Integer.class),
						any(Login.class))).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.getTruemoneyProfile(
						any(String.class))).thenReturn(profileMock);

			this.mockMvc
					.perform(post(confirmCreateProfileURL).content(mapper.writeValueAsBytes(mockData)).contentType(MediaType.APPLICATION_JSON))

					.andExpect(jsonPath("$.code").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andExpect(jsonPath("$..fullname").value("Apinya Ukachoke"))
					.andExpect(jsonPath("$..email").value("apinya@gmail.com"))
					.andExpect(jsonPath("$..currentBalance").exists())
					.andExpect(jsonPath("$..accessToken").exists())
					.andExpect(jsonPath("$..mobileNumber").value("0899999999"))
					.andDo(print());
	}

	@Test
	public void confirmCreateProfileFail() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String failedCode = "50001";
		String failedMessage = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "qwer");

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileno("0899999999");
		profileMock.setPassword("werw2345");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenThrow(new ServiceInventoryException(failedCode, failedMessage,
					failedNamespace));
		when(
				this.tmnProfileServiceMock.login(any(Integer.class),
						any(Login.class))).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.getTruemoneyProfile(
						any(String.class))).thenReturn(profileMock);

			this.mockMvc
					.perform(post(confirmCreateProfileURL).content(mapper.writeValueAsBytes(mockData)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andDo(print());
	}

	@Test
	public void confirmCreateProfileCannotLogin() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String failedCode = "40000";
		String failedMessage = "confirm umarket failed.";
		String failedNamespace = "TMN-PRODUCT";

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "qwer");
		mockData.put("checksum", "111111111111");

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileno("0899999999");
		profileMock.setPassword("werw2345");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenReturn(profileMock);

		when(
				this.tmnProfileServiceMock.login(any(Integer.class),
						any(Login.class))).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.login(any(Integer.class),
						any(Login.class))).thenThrow(
				new ServiceInventoryException(failedCode,
						failedMessage, failedNamespace));

			this.mockMvc
					.perform(post(confirmCreateProfileURL).content(mapper.writeValueAsBytes(mockData)).contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andDo(print());
	}

}
