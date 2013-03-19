package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
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
	String directDebitURL = String.format("/add-money/ewallet/banks/%s/%s", testUsername, fakeAccessToken);
	
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
		scb.setSourceOfFundId(fakeSourceOfFundId);
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
				get(directDebitURL).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.status").value("20000"))
					.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
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
				get(directDebitURL).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.status").value(failedCode))
					.andExpect(jsonPath("$.namespace").value(failedNamespace));
	}
}
