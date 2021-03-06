package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import th.co.truemoney.product.api.domain.TopupDirectDebitRequest;
import th.co.truemoney.product.api.domain.TopupQuotableRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
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
			this.sourceOfFundServiceMock.getUserDirectDebitSources(anyString())
		).thenReturn(returnedDirectDebitList);

		this.verifySuccess(
			this.doGET(getBankURL)
		).andExpect(jsonPath("$.data").exists()
		).andExpect(jsonPath("$..listOfBank").isArray()
		).andExpect(jsonPath("$..listOfBank[0].bankNameEn").value("Siam Commercial Bank")
		).andExpect(jsonPath("$..listOfBank[0].backgroundBankImageURL").value(containsString(".png")));
}

	@Test
	public void listUserRegisteredBankAccountInvalidAccessTokenFailed() throws Exception {
		String failedCode = "10001";
		String failedMessage = "AccessToken is expired";
		String failedNamespace = "TMN-SERVICE-INVENTORY";
		when(
				this.sourceOfFundServiceMock.getUserDirectDebitSources(anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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
				this.sourceOfFundServiceMock.getUserDirectDebitSources(anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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

		TopupDirectDebitRequest request = new TopupDirectDebitRequest();
		request.setAmount(new BigDecimal(100.00));
		request.setChecksum("00000000000000");
		request.setSourceOfFundID("B00001");

		when(
				this.topupServiceMock.createAndVerifyTopUpQuote(
						anyString(), any(BigDecimal.class),
						anyString())).thenReturn(topUpQuote);

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
				.andExpect(jsonPath("$..urlLogo").exists())
				.andExpect(jsonPath("$..promotionEn").exists())
				.andExpect(jsonPath("$..promotionTh").exists());
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
				this.topupServiceMock.createAndVerifyTopUpQuote(
						anyString(), any(BigDecimal.class),
						anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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
		quote.setSourceOfFund(fakeSourceOfFund);

		when(
				this.topupServiceMock.getTopUpQuoteDetails(anyString(),
						anyString())).thenReturn(quote);

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
				this.topupServiceMock.getTopUpQuoteDetails(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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

		OTP otp = new OTP();
		otp.setMobileNumber("0861234567");
		otp.setReferenceCode("abcd");
		otp.setOtpString("xxxxxx");

		when(this.transactionAuthenServiceMock.requestOTP(anyString(),
						anyString())).thenReturn(otp);

		TopUpQuote quote = new TopUpQuote();

		when(this.topupServiceMock.getTopUpQuoteDetails(anyString(),
						anyString())).thenReturn(quote);

		this.mockMvc
				.perform(post(sendOTPURL)
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
				this.transactionAuthenServiceMock.requestOTP(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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
		request.setOtpString("495959");

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate("");
		confirmationInfo.setTransactionID("10101010");


		when(
				this.transactionAuthenServiceMock.verifyOTP(anyString(),
						any(OTP.class), anyString())).thenReturn(TopUpQuote.Status.OTP_CONFIRMED);

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

		Mockito.verify(topupServiceMock).performTopUp(anyString(), anyString());
	}

	@Test
	public void confirmTransactionFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		ObjectMapper mapper = new ObjectMapper();
		OTP request = new OTP();
		request.setOtpString("495959");

		when(
				this.transactionAuthenServiceMock.verifyOTP(anyString(),any(OTP.class), anyString()))
				    .thenThrow(new ServiceInventoryException(400, failedCode, failedMessage,
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

		Mockito.verify(topupServiceMock, Mockito.never()).performTopUp(anyString(), anyString());
	}

	@Test
	public void checkStatusSuccess() throws Exception {
		when(
				this.topupServiceMock.getTopUpProcessingStatus(anyString(),
						 anyString())).thenReturn(th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status.SUCCESS);

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
				.andExpect(jsonPath("$..topupStatus").value("SUCCESS"));
	}

	@Test
	public void checkStatusFailed() throws Exception {
		String failedCode = "20000";
		String failedMessage = "Source of fund is null or empty";
		String failedNamespace = "TMN-SERVICE-INVENTORY";

		when(
				this.topupServiceMock.getTopUpProcessingStatus(anyString(), anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
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

		TopUpQuote quote = new TopUpQuote();
		quote.setStatus(TopUpQuote.Status.OTP_CONFIRMED);
		quote.setAccessTokenID(fakeAccessToken);
		quote.setAmount(new BigDecimal(100.00));
		quote.setID("1111");
		quote.setTopUpFee(new BigDecimal(10.00));
		quote.setSourceOfFund(fakeSourceOfFund);

		TopUpOrder order = new TopUpOrder(quote);

		order.setConfirmationInfo(confirmationInfo);
		order.setStatus(th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status.SUCCESS);


		when(
				this.topupServiceMock.getTopUpOrderResults(anyString(),
						 anyString())).thenReturn(order);

		when(this.profileServiceMock.getEwalletBalance(anyString())).thenReturn(new BigDecimal(10000.00));

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
				this.topupServiceMock.getTopUpOrderResults(anyString(), anyString())).thenThrow(
				new ServiceInventoryException(400, failedCode, failedMessage,
						failedNamespace));

		when(this.profileServiceMock.getEwalletBalance(anyString())).thenReturn(new BigDecimal(10000.00));

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

		ServiceInventoryException lessThanMinimumException = new ServiceInventoryException(400, "20001", "Amount less than minimum", "TMN-SERVICE-INVENTORY");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("minAmount", minimumAmount);
		data.put("maxAmount", maximumAmount);
		data.put("bankNameEn", bankNameEn);
		data.put("bankNameTh", bankNameTh);
		lessThanMinimumException.setData(data);

		when(
			this.topupServiceMock.createAndVerifyTopUpQuote(
				anyString(),
				any(BigDecimal.class),
				anyString())
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

		ServiceInventoryException moreThanMaximumExcepiion = new ServiceInventoryException(400, "20002", "Amount more than maximum", "TMN-SERVICE-INVENTORY");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("minAmount", minimumAmount);
		data.put("maxAmount", maximumAmount);
		data.put("bankNameEn", bankNameEn);
		data.put("bankNameTh", bankNameTh);
		moreThanMaximumExcepiion.setData(data);

		when(
			this.topupServiceMock.createAndVerifyTopUpQuote(
				anyString(),
				any(BigDecimal.class),
				anyString())
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
