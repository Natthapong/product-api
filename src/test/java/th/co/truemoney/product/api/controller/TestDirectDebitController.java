package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;

import th.co.truemoney.product.api.domain.TopupDirectDebitRequest;
import th.co.truemoney.product.api.domain.TopupQuotableRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDirectDebitController extends BaseTestController {

	String testUsername = "customer@truemoney.co.th";
	String fakeAccessToken = "0000000000000000000000000";
	String fakeBankAccountNumber = "1111111111111";
	String fakeSourceOfFundId = "B00001";
	String fakeSourceOfFundType = "DDB"; // suppose to be direct debit

	String getBankURL = String.format("/add-money/ewallet/banks/%s/%s",
			testUsername, fakeAccessToken);
	String createQuoteURL = String.format("/directdebit/quote/create/%s",
			fakeAccessToken);
	String getQuoteURL = String.format("/directdebit/quote/details/%s",
			fakeAccessToken);
	String sendOTPURL = String.format("/directdebit/order/create/%s",
			fakeAccessToken);
	String confirmTransactionURL = String.format(
			"/directdebit/order/confirm/%s", fakeAccessToken);
	String checkStatusURL = String.format("/directdebit/order/%s/status/%s",
			"1234", fakeAccessToken);
	String getTransactionURL = String.format("/directdebit/order/%s/details/%s",
			"1234", fakeAccessToken);



	@Test
	public void listUserRegisteredBankAccountSuccess() throws Exception {
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
				any(String.class))
			)
		.thenReturn(returnedDirectDebitList);

		this.mockMvc
				.perform(get(getBankURL).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..listOfBank").isArray())
				.andExpect(jsonPath("$..listOfBank[0].bankNameEn").value("Siam Commercial Bank"))
				.andDo(print());
	}

	@Test
	public void listUserRegisteredBankAccountInvalidAccessTokenFailed() throws Exception {
		String failedCode = "10001";
		String failedMessage = "AccessToken is expired";
		String failedNamespace = "TMN-SERVICE-INVENTORY";
		when(
				this.sourceOfFundServiceMock.getUserDirectDebitSources(
						any(String.class), any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(get(getBankURL).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void listUserBankAccountInvalidFormatUsername() throws Exception {
		String failedCode = "50001";
		String failedMessage = "Username is invalid format";
		String failedNamespace = "TMN-PRODUCT";
		when(
				this.sourceOfFundServiceMock.getUserDirectDebitSources(
						any(String.class), any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(get(getBankURL).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void createDirectdebitQuoteSuccess() throws Exception {
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
						any(String.class), any(QuoteRequest.class),
						any(String.class))).thenReturn(topUpQuote);

		this.mockMvc
				.perform(
						post(createQuoteURL).contentType(
								MediaType.APPLICATION_JSON).content(
								mapper.writeValueAsBytes(request)))
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
				.andExpect(jsonPath("$..bankNameEn").exists())
				.andExpect(jsonPath("$..bankNameTh").exists())
				.andExpect(jsonPath("$..sourceOfFundID").exists())
				.andExpect(jsonPath("$..accessToken").value(fakeAccessToken))
				.andExpect(jsonPath("$..urlLogo").exists());
	}

	@Test
	public void createDirectdebitQuoteFailed() throws Exception {
		String failedCode = "404";
		String failedMessage = "source of fund not found : 827b4639d1a74fdbae0201cc3b5fb3d";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		ObjectMapper mapper = new ObjectMapper();
		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100.00));
		request.setChecksum("00000000000000");

		when(
				this.topupServiceMock.createTopUpQuoteFromDirectDebit(
						any(String.class), any(QuoteRequest.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(
						post(createQuoteURL).contentType(
								MediaType.APPLICATION_JSON).content(
								mapper.writeValueAsBytes(request)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void getDirectdebitQuoteSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		DirectDebit fakeSourceOfFund = new DirectDebit();

		fakeSourceOfFund.setSourceOfFundID(fakeSourceOfFundId);
		fakeSourceOfFund.setSourceOfFundType(fakeSourceOfFundType);
		fakeSourceOfFund.setBankCode("SCB");
		fakeSourceOfFund.setBankAccountNumber(fakeBankAccountNumber);
		fakeSourceOfFund.setBankNameEn("Siam Commercial Bank");
		fakeSourceOfFund.setBankNameTh("ธนาคารไทยพานิชย์");

		TopupQuotableRequest request = new TopupQuotableRequest();
		request.setQuoteID("1234");

		TopUpQuote quote = new TopUpQuote();
		quote.setAccessTokenID(fakeAccessToken);
		quote.setAmount(new BigDecimal(100.00));
		quote.setID("111");
		quote.setTopUpFee(new BigDecimal(100.00));
		quote.setUsername("sfsdf");
		quote.setSourceOfFund(fakeSourceOfFund);

		when(
				this.topupServiceMock.getTopUpQuoteDetails(any(String.class),
						any(String.class))).thenReturn(quote);

		this.mockMvc
				.perform(
						get(getQuoteURL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsBytes(request)))
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
				.andExpect(jsonPath("$..bankNameEn").exists())
				.andExpect(jsonPath("$..bankNameTh").exists())
				.andExpect(jsonPath("$..sourceOfFundID").exists())
				.andExpect(jsonPath("$..accessToken").value(fakeAccessToken))
				.andExpect(jsonPath("$..urlLogo").exists());
	}

	@Test
	public void getDirectdebitQuoteFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		ObjectMapper mapper = new ObjectMapper();
		TopupQuotableRequest request = new TopupQuotableRequest();
		request.setQuoteID("1");

		when(
				this.topupServiceMock.getTopUpQuoteDetails(any(String.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(
						get(getQuoteURL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsBytes(request)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void sendOTPSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		DirectDebit fakeSourceOfFund = new DirectDebit();

		fakeSourceOfFund.setSourceOfFundID(fakeSourceOfFundId);
		fakeSourceOfFund.setSourceOfFundType(fakeSourceOfFundType);
		fakeSourceOfFund.setBankCode("SCB");
		fakeSourceOfFund.setBankAccountNumber(fakeBankAccountNumber);
		fakeSourceOfFund.setBankNameEn("Siam Commercial Bank");
		fakeSourceOfFund.setBankNameTh("ธนาคารไทยพานิชย์");

		TopupQuotableRequest request = new TopupQuotableRequest();
		request.setQuoteID("1234");

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate("");
		confirmationInfo.setTransactionID("10101010");

		TopUpOrder order = new TopUpOrder();
		order.setAccessTokenID(fakeAccessToken);
		order.setAmount(new BigDecimal(100.00));
		order.setID("1111");
		order.setConfirmationInfo(confirmationInfo);
		order.setOtpReferenceCode("FJFJ");
		order.setSourceOfFund(fakeSourceOfFund);
		order.setStatus(TopUpStatus.CONFIRMED);
		order.setTopUpFee(new BigDecimal(10.00));
		order.setUsername("username");

		when(
				this.topupServiceMock.requestPlaceOrder(any(String.class),
						any(String.class))).thenReturn(order);

		this.mockMvc
				.perform(
						post(sendOTPURL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsBytes(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..topupOrderID").exists())
				.andExpect(jsonPath("$..amount").exists())
				.andExpect(jsonPath("$..otpRefCode").exists());
	}

	@Test
	public void sendOTPFailed() throws Exception {
		String failedCode = "1004";
		String failedMessage = "quote not found";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		ObjectMapper mapper = new ObjectMapper();
		TopupQuotableRequest request = new TopupQuotableRequest();
		request.setQuoteID("1");

		when(
				this.topupServiceMock.requestPlaceOrder(any(String.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(
						post(sendOTPURL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsBytes(request)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void confirmTransactionSuccess() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		OTP request = new OTP();
		request.setChecksum("erte");
		request.setOtpString("495959");

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate("");
		confirmationInfo.setTransactionID("10101010");

		TopUpOrder order = new TopUpOrder();
		order.setStatus(TopUpStatus.CONFIRMED);

		when(
				this.topupServiceMock.confirmPlaceOrder(any(String.class),
						any(OTP.class), any(String.class))).thenReturn(order);

		this.mockMvc
				.perform(
						put(confirmTransactionURL).contentType(
								MediaType.APPLICATION_JSON).content(
								mapper.writeValueAsBytes(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..topupStatus").value("CONFIRMED"));
	}

	@Test
	public void confirmTransactionFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		ObjectMapper mapper = new ObjectMapper();
		OTP request = new OTP();
		request.setChecksum("erte");
		request.setOtpString("495959");

		when(
				this.topupServiceMock.confirmPlaceOrder(any(String.class),
						any(OTP.class), any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(
						put(confirmTransactionURL).contentType(
								MediaType.APPLICATION_JSON).content(
								mapper.writeValueAsBytes(request)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void checkStatusSuccess() throws Exception {
		when(
				this.topupServiceMock.getTopUpOrderStatus(any(String.class),
						 any(String.class))).thenReturn(TopUpStatus.CONFIRMED);

		this.mockMvc
				.perform(
						get(checkStatusURL).contentType(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..topupStatus").value("CONFIRMED"));
	}

	@Test
	public void checkStatusFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		when(
				this.topupServiceMock.getTopUpOrderStatus(any(String.class), any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		this.mockMvc
				.perform(
						get(checkStatusURL).contentType(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}

	@Test
	public void getTransactionDetailSuccess() throws Exception {
		DirectDebit fakeSourceOfFund = new DirectDebit();

		fakeSourceOfFund.setSourceOfFundID(fakeSourceOfFundId);
		fakeSourceOfFund.setSourceOfFundType(fakeSourceOfFundType);
		fakeSourceOfFund.setBankCode("SCB");
		fakeSourceOfFund.setBankAccountNumber(fakeBankAccountNumber);
		fakeSourceOfFund.setBankNameEn("Siam Commercial Bank");
		fakeSourceOfFund.setBankNameTh("ธนาคารไทยพานิชย์");

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate("");
		confirmationInfo.setTransactionID("10101010");

		TopUpOrder order = new TopUpOrder();
		order.setAccessTokenID(fakeAccessToken);
		order.setAmount(new BigDecimal(100.00));
		order.setID("1111");
		order.setConfirmationInfo(confirmationInfo);
		order.setOtpReferenceCode("FJFJ");
		order.setSourceOfFund(fakeSourceOfFund);
		order.setStatus(TopUpStatus.CONFIRMED);
		order.setTopUpFee(new BigDecimal(10.00));
		order.setUsername("username");

		when(
				this.topupServiceMock.getTopUpOrderDetails(any(String.class),
						 any(String.class))).thenReturn(order);

		when(this.profileServiceMock.getEwalletBalance(any(String.class))).thenReturn(new BigDecimal(10000.00));

		this.mockMvc
				.perform(
						get(getTransactionURL).contentType(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("20000"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists())
				.andExpect(jsonPath("$..amount").exists())
				.andExpect(jsonPath("$..currentBalance").exists())
				.andExpect(jsonPath("$..fee").exists())
				.andExpect(jsonPath("$..bankNumber").exists())
				.andExpect(jsonPath("$..bankNameEn").exists())
				.andExpect(jsonPath("$..bankNameTh").exists())
				.andExpect(jsonPath("$..urlLogo").exists());
	}

	@Test
	public void getTransactionDetailFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		when(
				this.topupServiceMock.getTopUpOrderDetails(any(String.class), any(String.class))).thenThrow(
				new ServiceInventoryException(failedCode, failedMessage,
						failedNamespace));

		when(this.profileServiceMock.getEwalletBalance(any(String.class))).thenReturn(new BigDecimal(10000.00));

		this.mockMvc
				.perform(
						get(getTransactionURL).contentType(
								MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(failedCode))
				.andExpect(jsonPath("$.namespace").value(failedNamespace))
				.andExpect(jsonPath("$.messageEn").exists())
				.andExpect(jsonPath("$.messageTh").exists());
	}
	@Test
	public void createDirectDebitTopupQuoteReturnInvalidAmountExcepiton20001() throws Exception {
		final String bankNameEn = "Siam Commerical Bank";
		final String bankNameTh = "ธนาคารไทยพานิชย์";
		final BigDecimal minimumAmount = new BigDecimal(300);
		final BigDecimal maximumAmount = new BigDecimal(999);

		ServiceInventoryException lessThanMinimumException = new ServiceInventoryException("20001", "Amount less than minimum", "TMN-SERVICE-INVENTORY");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("minAmount", minimumAmount);
		data.put("maxAmount", maximumAmount);
		data.put("bankNameEn", bankNameEn);
		data.put("bankNameTh", bankNameTh);
		lessThanMinimumException.setData(data);

		when(
			this.topupServiceMock.createTopUpQuoteFromDirectDebit(
				any(String.class),
				any(QuoteRequest.class),
				any(String.class))
		).thenThrow(lessThanMinimumException);

		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100));
		request.setSourceOfFundID("source-of-fund-id");
		request.setChecksum("-checksum-string-");

		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(
				post("/directdebit/quote/create/_token_string_").contentType(
						MediaType.APPLICATION_JSON).content(
						mapper.writeValueAsBytes(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.code").value("20001"))
		.andExpect(jsonPath("$.namespace").value("TMN-SERVICE-INVENTORY"))
		.andExpect(jsonPath("$.titleEn").value(containsString(bankNameEn)))
		.andExpect(jsonPath("$.titleTh").value(containsString(bankNameTh)))
		.andExpect(jsonPath("$.messageEn").value(containsString(minimumAmount.toString())))
		.andExpect(jsonPath("$.messageEn").value(containsString(maximumAmount.toString())))
		.andExpect(jsonPath("$.messageTh").value(containsString(minimumAmount.toString())))
		.andExpect(jsonPath("$.messageTh").value(containsString(maximumAmount.toString())));
	}

	@Test
	public void createDirectDebitTopupQuoteReturnInvalidAmountExcepiton20002() throws Exception {
		final String bankNameEn = "Siam Commerical Bank";
		final String bankNameTh = "ธนาคารไทยพานิชย์";
		final BigDecimal minimumAmount = new BigDecimal(100);
		final BigDecimal maximumAmount = new BigDecimal(777);

		ServiceInventoryException moreThanMaximumExcepiion = new ServiceInventoryException("20002", "Amount more than maximum", "TMN-SERVICE-INVENTORY");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("minAmount", minimumAmount);
		data.put("maxAmount", maximumAmount);
		data.put("bankNameEn", bankNameEn);
		data.put("bankNameTh", bankNameTh);
		moreThanMaximumExcepiion.setData(data);

		when(
			this.topupServiceMock.createTopUpQuoteFromDirectDebit(
				any(String.class),
				any(QuoteRequest.class),
				any(String.class))
		).thenThrow(moreThanMaximumExcepiion);

		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100));
		request.setSourceOfFundID("source-of-fund-id");
		request.setChecksum("-checksum-string-");

		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(
				post("/directdebit/quote/create/_token_string_").contentType(
						MediaType.APPLICATION_JSON).content(
						mapper.writeValueAsBytes(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.code").value("20002"))
		.andExpect(jsonPath("$.namespace").value("TMN-SERVICE-INVENTORY"))
		.andExpect(jsonPath("$.titleEn").value(containsString(bankNameEn)))
		.andExpect(jsonPath("$.titleTh").value(containsString(bankNameTh)))
		.andExpect(jsonPath("$.messageEn").value(containsString(minimumAmount.toString())))
		.andExpect(jsonPath("$.messageEn").value(containsString(maximumAmount.toString())))
		.andExpect(jsonPath("$.messageTh").value(containsString(minimumAmount.toString())))
		.andExpect(jsonPath("$.messageTh").value(containsString(maximumAmount.toString())));
	}
}
