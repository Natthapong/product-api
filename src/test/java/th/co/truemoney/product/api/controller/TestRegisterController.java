package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestRegisterController {

	private MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();
	
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
	public void validateEmailSuccess() throws Exception {
		String email = "apinya@gmail.com";

		Map<String, String> data = new HashMap<String, String>();
		data.put("email","apinya@gmail.com");

		when(
				this.tmnProfileServiceMock.validateEmail(anyInt(), any(String.class))
		).thenReturn(email);

		ResultActions result = doPost("/ewallet/profiles/validate-email", data);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.code").value("20000"));
		result.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
		result.andExpect(jsonPath("$..email").value("apinya@gmail.com"));
	}

	@Test
	public void validateEmailInvalidFormat() throws Exception {
		String failedCode = "40000";
		String failedMessage = "Invalid email format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String, String> data = new HashMap<String, String>();
		data.put("email","apinya@gmail.com");

		when(
				this.tmnProfileServiceMock.validateEmail(anyInt(), any(String.class))
		).thenThrow(new ServiceInventoryException(400, failedCode, failedMessage,failedNamespace));
		
		ResultActions result = doPost("/ewallet/profiles/validate-email", data);
		
		result.andExpect(status().isInternalServerError());
		result.andExpect(jsonPath("$.code").value(failedCode));
		result.andExpect(jsonPath("$.namespace").value(failedNamespace));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void createProfileSuccess() throws Exception {
		Map<String, String> tmnProfile = new HashMap<String, String>();
		tmnProfile.put("email","apinya@gmail.com");
		tmnProfile.put("password","werw2345");
		tmnProfile.put("mobileNumber","0899999999");
		tmnProfile.put("fullname","Apinya Ukachoke");

		OTP otp = new OTP();
		otp.setMobileNumber("0899999999");
		otp.setOtpString("123456");
		otp.setReferenceCode("wert");

		when(
			this.tmnProfileServiceMock.createProfile(anyInt(), any(TmnProfile.class))
		).thenReturn(otp);
		
		ResultActions result = doPost("/ewallet/profiles", tmnProfile);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.code").value("20000"));
		result.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void createProfileFail() throws Exception {
		String failedCode = "50001";
		String failedMessage = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String, String> tmnProfile = new HashMap<String, String>();
		tmnProfile.put("email","apinya@gmail.com");
		tmnProfile.put("password","werw2345");
		tmnProfile.put("mobileNumber","0899999999");
		tmnProfile.put("fullname","Apinya Ukachoke");

		when(
				this.tmnProfileServiceMock.createProfile(anyInt(), any(TmnProfile.class)) )
			.thenThrow(new ServiceInventoryException(400, failedCode, failedMessage,
					failedNamespace));
		
		ResultActions result = doPost("/ewallet/profiles", tmnProfile);
		
		result.andExpect(status().isInternalServerError());
		result.andExpect(jsonPath("$.code").value(failedCode));
		result.andExpect(jsonPath("$.namespace").value(failedNamespace));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void confirmCreateProfileSuccess() throws Exception {
		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileNumber("0899999999");
		profileMock.setPassword("werw2345");
		profileMock.setBalance(new BigDecimal(0.00));
		profileMock.setStatus(0);
		profileMock.setType("C");
		profileMock.setThaiID("1212121212121");

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "111111");
		mockData.put("otpRefCode", "qwer");
		mockData.put("loginSecret", "111111111111");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenReturn(profileMock);

		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
		).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.getTruemoneyProfile(
						any(String.class))).thenReturn(profileMock);
		
		ResultActions result = doPost("/ewallet/profiles/verify-otp", mockData);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.code").value("20000"));
		result.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void confirmCreateProfileFail() throws Exception {
		String failedCode = "50001";
		String failedMessage = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "111111");
		mockData.put("otpRefCode", "qwer");
		//mockData.put("loginSecret", "111111111111");

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileNumber("0899999999");
		profileMock.setPassword("werw2345");
		
		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenThrow(new ServiceInventoryException(400, failedCode, failedMessage,
					failedNamespace));
		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
			).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.getTruemoneyProfile(
						any(String.class))).thenReturn(profileMock);
		
		ResultActions result = doPost("/ewallet/profiles/verify-otp", mockData);
		
		result.andExpect(status().isInternalServerError());
		result.andExpect(jsonPath("$.code").value(failedCode));
		result.andExpect(jsonPath("$.namespace").value(failedNamespace));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void confirmCreateProfileOTPNotFound() throws Exception {
		String failedCode = "1000";
		String failedMessage = "send OTP failed.";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "123456");
		mockData.put("otpRefCode", "qwer");
		mockData.put("loginSecret", "1111111");

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileNumber("0899999999");
		profileMock.setPassword("werw2345");
		profileMock.setThaiID("1212121212121");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenThrow(
					new ServiceInventoryException(400, failedCode,
							failedMessage, failedNamespace));

		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
			).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
			).thenReturn("");
		
		ResultActions result = doPost("/ewallet/profiles/verify-otp", mockData);
		
		result.andExpect(status().isInternalServerError());
		result.andExpect(jsonPath("$.code").value(failedCode));
		result.andExpect(jsonPath("$.namespace").value(failedNamespace));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}

	@Test
	public void confirmCreateProfileInvalidMobileFormat() throws Exception {
		
		String failedCode = "1000";
		String failedMessage = "send OTP failed.";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		Map<String,String> mockData = new HashMap<String, String>();
		mockData.put("mobileNumber", "0899999999");
		mockData.put("otpString", "123456");
		mockData.put("otpRefCode", "qwer");
		mockData.put("loginSecret", "1111111");

		TmnProfile profileMock = new TmnProfile();
		profileMock.setEmail("apinya@gmail.com");
		profileMock.setFullname("Apinya Ukachoke");
		profileMock.setMobileNumber("0899999999");
		profileMock.setPassword("werw2345");
		profileMock.setThaiID("1212121212121");

		when(
				this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class)) )
			.thenThrow(
					new ServiceInventoryException(400, failedCode,
							failedMessage, failedNamespace));

		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
			).thenReturn("token-string");

		when(
				this.tmnProfileServiceMock.login(
						any(EWalletOwnerCredential.class),
						any(ClientCredential.class))
			).thenReturn("");
		
		ResultActions result = doPost("/ewallet/profiles/verify-otp", mockData);
		
		result.andExpect(status().isInternalServerError());
		result.andExpect(jsonPath("$.code").value(failedCode));
		result.andExpect(jsonPath("$.namespace").value(failedNamespace));
		result.andExpect(jsonPath("$.messageEn").exists());
		result.andExpect(jsonPath("$.messageTh").exists());
		result.andExpect(jsonPath("$.data").exists());
	}
	
	private ResultActions doPost(String url, Map<String,String> requestData) throws Exception {
		return this.mockMvc.perform(post(url).content(mapper.writeValueAsBytes(requestData)).contentType(MediaType.APPLICATION_JSON));
	}

}
