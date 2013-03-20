package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.print.Printable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import th.co.truemoney.product.api.domain.TopupDirectDebitRequest;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestDirectDebitController {
	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	SourceOfFundService sourceOfFundServiceMock;
	
	@Autowired
	TopUpService topupServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.sourceOfFundServiceMock = wac.getBean(SourceOfFundService.class);
		this.topupServiceMock = wac.getBean(TopUpService.class);
	}

	@After
	public void tierDown() {
		reset(this.sourceOfFundServiceMock);
		reset(this.topupServiceMock);
	}
	
	String testUsername = "customer@truemoney.co.th";
	String fakeAccessToken = "0000000000000000000000000";
	String getBankURL = String.format("/add-money/ewallet/banks/%s/%s", testUsername, fakeAccessToken);
	String createQuoteURL = String.format("/directdebit/quote/create/%s", fakeAccessToken);
	
	@Test
	public void listUserRegisteredBankAccountSuccess() throws Exception {
		
		String fakeBankAccountNumber = "XXXX XXXX XXXX 1234"; 
		String fakeSourceOfFundId = "B00001";
		String fakeSourceOfFundType = "DDB"; //suppose to be direct debit
		
		DirectDebit scb = new DirectDebit();
		scb.setBankCode("SCB");
		scb.setBankAccountNumber(fakeBankAccountNumber);
		scb.setBankNameEn("Siam Commercial Bank");
		scb.setBankNameTh("ธนาคารไทยพานิชย์");
		scb.setMaxAmount(new BigDecimal(5000.00));
		scb.setMinAmount(new BigDecimal(500.00));
		scb.setSourceOfFundID(fakeSourceOfFundId);
		scb.setSourceOfFundType(fakeSourceOfFundType);
		
		List<DirectDebit> returnedDirectDebitList = new ArrayList<DirectDebit>();
		returnedDirectDebitList.add(scb);
		
		when(
			this.sourceOfFundServiceMock.getUserDirectDebitSources(
				any(String.class), 
				any(String.class)
			)
		).thenReturn(returnedDirectDebitList);
		
		this.mockMvc.perform(
				get(getBankURL).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andExpect(jsonPath("$..listOfBank").isArray())
					.andExpect(jsonPath("$..listOfBank[0].bankNameEN").value("Siam Commercial Bank"));
	}
	
	@Test
	public void listUserRegisteredBankAccountFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage  = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";
		when(
			this.sourceOfFundServiceMock.getUserDirectDebitSources(
				any(String.class), 
				any(String.class))
			)
		.thenThrow(new ServiceInventoryException(failedCode, failedMessage, failedNamespace));
		
		this.mockMvc.perform(
				get(getBankURL).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists());
	}
	
	@Test
	public void listUserBankAccountInvalidFormatUsername() throws Exception {
		String failedCode = "50001";
		String failedMessage  = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";
		when(
			this.sourceOfFundServiceMock.getUserDirectDebitSources(
				any(String.class), 
				any(String.class))
			)
		.thenThrow(new ServiceInventoryException(failedCode, failedMessage, failedNamespace));
		
		this.mockMvc.perform(
				get(getBankURL).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists());
	}
	
	@SuppressWarnings("serial")
	@Test@Ignore
	public void createDirectdebitQuoteSuccess() throws Exception {
		
		String fakeBankAccountNumber = "XXXX XXXX XXXX 1234"; 
		String fakeSourceOfFundId = "B00001";
		String fakeSourceOfFundType = "DDB"; //suppose to be direct debit
		
		ObjectMapper mapper = new ObjectMapper();
		
		DirectDebit fakeSourceOfFund = new DirectDebit();
		
		fakeSourceOfFund.setSourceOfFundID(fakeSourceOfFundId);
		fakeSourceOfFund.setSourceOfFundType(fakeSourceOfFundType);
		fakeSourceOfFund.setBankCode("SCB");
		fakeSourceOfFund.setBankAccountNumber(fakeBankAccountNumber);
		fakeSourceOfFund.setBankNameEn("Siam Commercial Bank");
		fakeSourceOfFund.setBankNameTh("ธนาคารไทยพานิชย์");
		
		TopUpQuote topUpQuote = new TopUpQuote();
		
		topUpQuote.setAmount(new BigDecimal(5000.00));
		topUpQuote.setSourceOfFund(fakeSourceOfFund);
		topUpQuote.setAccessTokenID(fakeAccessToken);
		topUpQuote.setID("342");
		topUpQuote.setTopUpFee(new BigDecimal(10.00));
		topUpQuote.setUsername("sdfsdf");
		
		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100.00));
		request.setChecksum("00000000000000");
		request.setSourceOfFundID("B00001");
		
		when(
			this.topupServiceMock.createTopUpQuoteFromDirectDebit(
				any(String.class),any(QuoteRequest.class),any(String.class)
			)
		).thenReturn(topUpQuote);
		
		this.mockMvc.perform(post(createQuoteURL)
				.contentType(MediaType.APPLICATION_JSON).content(
						mapper.writeValueAsBytes(request))).andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists())
					.andExpect(jsonPath("$.data").exists())
					.andExpect(jsonPath("$..quoteID").exists())
					.andExpect(jsonPath("$..amount").exists())
					.andExpect(jsonPath("$..fee").exists())
					.andExpect(jsonPath("$..bankNumber").exists())
					.andExpect(jsonPath("$..bankNameEN").exists())
					.andExpect(jsonPath("$..bankNameTH").exists())
					.andExpect(jsonPath("$..sourceOfFundID").exists())
					.andExpect(jsonPath("$..accessToken").value(fakeAccessToken))
					.andExpect(jsonPath("$..urlLogo").exists());
	}
	
	@Test
	public void createDirectdebitQuoteFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage  = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";
		
		ObjectMapper mapper = new ObjectMapper();
		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100.00));
		request.setChecksum("00000000000000");
		request.setSourceOfFundID("1");
		
		when(
				this.topupServiceMock.createTopUpQuoteFromDirectDebit(
						any(String.class),any(QuoteRequest.class),any(String.class))
			)
		.thenThrow(new ServiceInventoryException(failedCode, failedMessage, failedNamespace));
		
		this.mockMvc.perform(
				post(createQuoteURL)
				.contentType(MediaType.APPLICATION_JSON).content(
						mapper.writeValueAsBytes(request)))
					.andExpect(status().isInternalServerError())
					.andExpect(jsonPath("$.code").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace))
					.andExpect(jsonPath("$.messageEn").exists())
					.andExpect(jsonPath("$.messageTh").exists());
	}
	
	
}
