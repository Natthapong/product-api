package th.co.truemoney.product.api.controller;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class BaseTestController {
	
	protected MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;
	
	@Autowired
	protected SourceOfFundService sourceOfFundServiceMock;

	@Autowired
	protected TopUpService topupServiceMock;

	@Autowired
	protected TmnProfileService profileServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.sourceOfFundServiceMock = wac.getBean(SourceOfFundService.class);
		this.topupServiceMock = wac.getBean(TopUpService.class);
		this.profileServiceMock = wac.getBean(TmnProfileService.class);
	}
	
	@After
	public void tierDown() {
		reset(this.sourceOfFundServiceMock);
		reset(this.topupServiceMock);
		reset(this.profileServiceMock);
	}
	
	protected ResultActions doPOST(String url, Object reqBody) throws JsonProcessingException, Exception {
		ObjectMapper mapper = new ObjectMapper();
		return this.mockMvc.perform(
				post(url).contentType(MediaType.APPLICATION_JSON)
						 .content(mapper.writeValueAsBytes(reqBody)));
	}
	
	protected void verifySuccess(ResultActions actions) throws Exception {
		actions.andExpect(status().isOk())
		  	   .andExpect(jsonPath("$.code").value("20000"))
		  	   .andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
		  	   .andExpect(jsonPath("$.messageEn").value("Success"))
		  	   .andExpect(jsonPath("$.messageTh").exists()); //TODO check the exact message sent out
	}
	
	protected void verifyBadRequest(ResultActions actions) throws Exception {
		actions.andExpect(status().isBadRequest())
		  	   .andExpect(jsonPath("$.code").exists())
		  	   .andExpect(jsonPath("$.namespace").exists())
		  	   .andExpect(jsonPath("$.messageEn").exists())
		  	   .andExpect(jsonPath("$.messageTh").exists()); //TODO check the exact message sent out
	}
	
	protected ResultActions verifyFailed(ResultActions actions) throws Exception {
		return actions.andExpect(status().isInternalServerError())
		  	   .andExpect(jsonPath("$.code").exists())
		  	   .andExpect(jsonPath("$.namespace").exists())
		  	   .andExpect(jsonPath("$.messageEn").exists())
		  	   .andExpect(jsonPath("$.messageTh").exists()); //TODO check the exact message sent out
	}
}
