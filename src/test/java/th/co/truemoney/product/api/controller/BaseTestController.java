package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.product.api.handler.TransferActivityDetailViewHandler;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public abstract class BaseTestController {

	protected MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected DirectDebitSourceOfFundService sourceOfFundServiceMock;

	@Autowired
	protected TopUpService topupServiceMock;

	@Autowired
	protected TmnProfileService profileServiceMock;

	@Autowired
	protected P2PTransferService p2pTransferServiceMock;

	@Autowired
	protected BillPaymentService billPaymentServiceMock;

	@Autowired
	protected TopUpMobileService topUpMobileServiceMock;
	
	@Autowired
	protected BuyProductService buyProductServiceMock;

	@Autowired
	protected ActivityService activityServiceMock;

	@Autowired
	protected FavoriteService favoriteServiceMock;

	@Autowired
	protected TransactionAuthenService transactionAuthenServiceMock;
	
	@Autowired
	protected TransferActivityDetailViewHandler transferActivityDetailViewMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.sourceOfFundServiceMock = wac
				.getBean(DirectDebitSourceOfFundService.class);
		this.topupServiceMock = wac.getBean(TopUpService.class);
		this.profileServiceMock = wac.getBean(TmnProfileService.class);
		this.p2pTransferServiceMock = wac.getBean(P2PTransferService.class);
		this.billPaymentServiceMock = wac.getBean(BillPaymentService.class);
		this.topUpMobileServiceMock = wac.getBean(TopUpMobileService.class);
		this.buyProductServiceMock = wac.getBean(BuyProductService.class);
		this.activityServiceMock = wac.getBean(ActivityService.class);
		this.favoriteServiceMock = wac.getBean(FavoriteService.class);
		this.transactionAuthenServiceMock = wac.getBean(TransactionAuthenService.class);
	}

	@After
	public void tearDown() {
		reset(this.sourceOfFundServiceMock);
		reset(this.topupServiceMock);
		reset(this.profileServiceMock);
		reset(this.p2pTransferServiceMock);
		reset(this.billPaymentServiceMock);
		reset(this.topUpMobileServiceMock);
		reset(this.buyProductServiceMock);
		reset(this.activityServiceMock);
		reset(this.favoriteServiceMock);
   	    reset(this.transactionAuthenServiceMock);
	}

	ObjectMapper mapper = new ObjectMapper();

	protected ResultActions doPOST(String url, Object reqBody) throws Exception {
		return this.mockMvc.perform(post(url).contentType(
				MediaType.APPLICATION_JSON).content(
				mapper.writeValueAsBytes(reqBody)));
	}

	protected ResultActions doPOST(String url) throws Exception {
		return this.mockMvc.perform(post(url).contentType(
				MediaType.APPLICATION_JSON));
	}

	protected ResultActions doGET(String url, Object reqBody) throws Exception {
		return this.mockMvc.perform(get(url).contentType(
				MediaType.APPLICATION_JSON).content(
				mapper.writeValueAsBytes(reqBody)));
	}

	protected ResultActions doGET(String url) throws Exception {
		return this.mockMvc.perform(get(url).contentType(
				MediaType.APPLICATION_JSON));
	}

	protected ResultActions doPUT(String url, Object reqBody) throws Exception {
		return this.mockMvc.perform(put(url).contentType(
				MediaType.APPLICATION_JSON).content(
				mapper.writeValueAsBytes(reqBody)));
	}
	
	protected ResultActions doDELETE(String url, Object reqBody) throws Exception {
		return this.mockMvc.perform(delete(url).contentType(
				MediaType.APPLICATION_JSON).content(
				mapper.writeValueAsBytes(reqBody)));
	}

	protected ResultActions verifySuccess(ResultActions actions)
			throws Exception {
		return actions.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").value("Success"))
				.andExpect(jsonPath("$.messageTh").exists()); // TODO check the
																// exact message
																// sent out
	}

	protected void verifyBadRequest(ResultActions actions) throws Exception {
		actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(containsString("5000")))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists()); // TODO check the
																// exact message
																// sent out
	}

	protected ResultActions verifyFailed(ResultActions actions)
			throws Exception {
		return actions.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").exists())
				.andExpect(jsonPath("$.namespace").exists())
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists()); // TODO check the
																// exact message
																// sent out
	}
}
