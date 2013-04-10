package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestP2PController extends BaseTestController {

	String fakeAccessToken = "111111111111";
	String transferMoneyURL = String.format("/transfer/draft-transaction/%s",
			fakeAccessToken);
	String verifyTransferURL = String.format(
			"/transfer/draft-transaction/%s/send-otp/%s", "1111111111111",
			fakeAccessToken);
	String confirmTransferURL = String.format("/transfer/transaction/%s/%s",
			"1111111111111", fakeAccessToken);
	String statusTransferURL = String.format(
			"/transfer/transaction/%s/status/%s", "1111111111111",
			fakeAccessToken);
	String getTransferDetailURL = String.format("/transfer/transaction/%s/%s",
			"1111111111111", fakeAccessToken);

	@After
	public void tierDown() {
		reset(this.p2pTransferServiceMock);
	}
	
	@Test
	public void createDraftTransactionSuccess() throws Exception {
		P2PDraftTransaction draftTransaction = new P2PDraftTransaction();
		draftTransaction.setAccessTokenID(fakeAccessToken);
		draftTransaction.setAmount(new BigDecimal(100.00));
		draftTransaction.setFullname("Apinya Ukachoke");
		draftTransaction.setID("11111");
		draftTransaction.setMobileNumber("0899999999");
		draftTransaction.setOtpReferenceCode("qwer");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("amount", "100.00");

		when(
				p2pTransferServiceMock.createDraftTransaction(anyString(),
						any(BigDecimal.class), anyString())).thenReturn(
				draftTransaction);

		this.verifySuccess(this.doPOST(transferMoneyURL, data));
	}

	@Test
	public void createDraftTransactionFail() throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("amount", "100.00");

		when(
				p2pTransferServiceMock.createDraftTransaction(anyString(),
						any(BigDecimal.class), anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPOST(transferMoneyURL, data))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void verifyTransferSuccess() throws Exception {
		OTP otp = new OTP();
		otp.setOtpString("123456");
		otp.setMobileNumber("0899999999");
		otp.setReferenceCode("qwer");

		P2PDraftTransaction draftTransaction = new P2PDraftTransaction();
		draftTransaction.setAccessTokenID(fakeAccessToken);
		draftTransaction.setAmount(new BigDecimal(100.00));
		draftTransaction.setFullname("Apinya Ukachoke");
		draftTransaction.setID("11111");
		draftTransaction.setMobileNumber("0899999999");
		draftTransaction.setOtpReferenceCode("qwer");

		when(
				p2pTransferServiceMock.sendOTP(any(String.class),
						any(String.class))).thenReturn(otp);

		when(
				p2pTransferServiceMock.getDraftTransactionDetails(
						any(String.class), any(String.class))).thenReturn(
				draftTransaction);

		this.verifySuccess(this.doPUT(verifyTransferURL));
	}

	@Test
	public void verifyTransferFail() throws Exception {
		P2PDraftTransaction draftTransaction = new P2PDraftTransaction();
		draftTransaction.setAccessTokenID(fakeAccessToken);
		draftTransaction.setAmount(new BigDecimal(100.00));
		draftTransaction.setFullname("Apinya Ukachoke");
		draftTransaction.setID("11111");
		draftTransaction.setMobileNumber("0899999999");
		draftTransaction.setOtpReferenceCode("qwer");

		when(
				p2pTransferServiceMock.sendOTP(any(String.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		when(
				p2pTransferServiceMock.getDraftTransactionDetails(
						any(String.class), any(String.class))).thenReturn(
				draftTransaction);

		this.verifyFailed(this.doPUT(verifyTransferURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void confirmTransferSuccess() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("otpString", "123456");
		data.put("otpRefCode", "qwer");

		when(
				p2pTransferServiceMock.confirmDraftTransaction(
						any(String.class),
						any(OTP.class), 
						any(String.class))
		).thenReturn(DraftTransaction.Status.OTP_CONFIRMED);

		this.verifySuccess(this.doPOST(confirmTransferURL, data)).andExpect(
				jsonPath("$..transferStatus").exists());
	}

	@Test
	public void confirmTransferFail() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("otpString", "123456");
		data.put("otpRefCode", "qwer");

		when(
				p2pTransferServiceMock.confirmDraftTransaction(any(String.class),
						any(OTP.class), any(String.class))).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPOST(confirmTransferURL, data))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void checkStatusSuccess() throws Exception {
		when(
				p2pTransferServiceMock.getTransactionStatus(any(String.class),
						any(String.class))).thenReturn(
				Transaction.Status.PROCESSING);

		this.verifySuccess(this.doGET(statusTransferURL)).andExpect(
				jsonPath("$..transferStatus").exists());
	}

	@Test
	public void checkStatusFail() throws Exception {
		when(
				p2pTransferServiceMock.getTransactionStatus(any(String.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(statusTransferURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test@Ignore
	public void getTransferDetailSuccess() throws Exception {
		P2PDraftTransaction draftTransaction = new P2PDraftTransaction();
		draftTransaction.setAccessTokenID(fakeAccessToken);
		draftTransaction.setAmount(new BigDecimal(100.00));
		draftTransaction.setFullname("Apinya Ukachoke");
		draftTransaction.setID("1111");
		draftTransaction.setMobileNumber("0899999999");
		draftTransaction.setOtpReferenceCode("qwer");
		draftTransaction.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		
		P2PTransaction transaction = new P2PTransaction(draftTransaction);
		transaction.setStatus(Transaction.Status.SUCCESS);
		
		//set requestbody for doGET
		when(
				p2pTransferServiceMock.getTransactionResult(any(String.class),
						any(String.class))).thenReturn(transaction);
		
		when(
				profileServiceMock.getEwalletBalance(any(String.class))).thenReturn(new BigDecimal(100.00));

		
		this.verifySuccess(this.doGET(getTransferDetailURL))
				.andExpect(jsonPath("$..mobileNumber").exists())
				.andExpect(jsonPath("$..amount").exists())
				.andExpect(jsonPath("$..recipientName").exists())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists())
				.andExpect(jsonPath("$..currentBalance").exists());
	}

	@Test
	public void getTransferDetailFail() throws Exception {
		when(
				p2pTransferServiceMock.getTransactionResult(any(String.class),
						any(String.class))).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getTransferDetailURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

}
