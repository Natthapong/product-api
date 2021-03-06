package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

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
	String unknownMessage = "กรุณารอสักครู่ แล้วทํารายการใหม่อีกครั้ง <TMN-PRODUCT.>";

	@Test
	public void createTransferDraftSuccess() throws Exception {
		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("11111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("amount", "100.00");

		when(p2pTransferServiceMock.createAndVerifyTransferDraft(
				anyString(),
				any(BigDecimal.class),
				anyString())
			).thenReturn(transferDraft);

		this.verifySuccess(this.doPOST(transferMoneyURL, data)).andExpect(jsonPath("$..mobileNumber").value("089-999-9999"));
	}

	@Test
	public void createTransferDraftFail() throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("amount", "100.00");

		when(p2pTransferServiceMock.createAndVerifyTransferDraft(
				anyString(),
				any(BigDecimal.class),
				anyString())
			).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPOST(transferMoneyURL, data))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value(unknownMessage))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void verifyTransferSuccess() throws Exception {
		OTP otp = new OTP();
		otp.setOtpString("123456");
		otp.setMobileNumber("0899999999");
		otp.setReferenceCode("qwer");

		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("11111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");

		when(transactionAuthenServiceMock.requestOTP(
				any(String.class),
				any(String.class))
			).thenReturn(otp);
		
		when(p2pTransferServiceMock.getTransferDraftDetails(
				any(String.class),
				any(String.class))
			).thenReturn(transferDraft);

		this.verifySuccess(this.doPUT(verifyTransferURL, new HashMap<String, Object>()));
	}

	@Test
	public void verifyTransferWithPersonalMessageSuccess() throws Exception {
		OTP otp = new OTP();
		otp.setOtpString("123456");
		otp.setMobileNumber("0899999999");
		otp.setReferenceCode("qwer");

		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("11111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");
		transferDraft.setMessage("Hello,World");

		when(transactionAuthenServiceMock.requestOTP(
				any(String.class),
				any(String.class))
			).thenReturn(otp);
		
		when(p2pTransferServiceMock.getTransferDraftDetails(
				any(String.class),
				any(String.class))
			).thenReturn(transferDraft);
		
		Mockito.doNothing().when(p2pTransferServiceMock).setPersonalMessage(anyString(), anyString(), anyString());
		
		this.verifySuccess(this.doPUT(verifyTransferURL, new HashMap<String, Object>()));
	}
	

	@Test
	public void verifyTransferWithPersonalMessageFail() throws Exception {
		OTP otp = new OTP();
		otp.setOtpString("123456");
		otp.setMobileNumber("0899999999");
		otp.setReferenceCode("qwer");

		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("11111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");
		transferDraft.setMessage("Hello,World");

		when(transactionAuthenServiceMock.requestOTP(
				any(String.class),
				any(String.class))
			).thenReturn(otp);
		
		when(p2pTransferServiceMock.getTransferDraftDetails(
				any(String.class),
				any(String.class))
			).thenReturn(transferDraft);
		
		Mockito.doThrow(new ServiceInventoryException(500, "", "", "TMN-PRODUCT")).when(p2pTransferServiceMock).setPersonalMessage(anyString(), anyString(), anyString());
		
		Map<String,String> parameters =  new HashMap<String, String>();
		parameters.put("personalMessage", "hello");
		
		this.verifyFailed(this.doPUT(verifyTransferURL,parameters));
	}
	
	
	
	@Test
	public void verifyTransferFail() throws Exception {
		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("11111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");

		when(transactionAuthenServiceMock.requestOTP(
				any(String.class),
				any(String.class))
			).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		when(p2pTransferServiceMock.getTransferDraftDetails(
				any(String.class),
				any(String.class))
			).thenReturn(transferDraft);

		this.verifyFailed(this.doPUT(verifyTransferURL, new HashMap<String, Object>()))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value(unknownMessage))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void confirmTransferSuccess() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("otpString", "123456");
		data.put("otpRefCode", "qwer");

		when(transactionAuthenServiceMock.verifyOTP(
					any(String.class),
					any(OTP.class),
					any(String.class))
		).thenReturn(P2PTransferDraft.Status.OTP_CONFIRMED);

		this.verifySuccess(this.doPOST(confirmTransferURL, data)).andExpect(
				jsonPath("$..transferStatus").exists());

		Mockito.verify(p2pTransferServiceMock).performTransfer(anyString(), anyString());
	}

	@Test
	public void confirmTransferFail() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("otpString", "123456");
		data.put("otpRefCode", "qwer");

		when(transactionAuthenServiceMock.verifyOTP(
				any(String.class),
				any(OTP.class),
				any(String.class)))
			.thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPOST(confirmTransferURL, data))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value(unknownMessage))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));

		Mockito.verify(p2pTransferServiceMock, Mockito.never()).performTransfer(anyString(), anyString());
	}

	@Test
	public void checkStatusSuccess() throws Exception {

		when(p2pTransferServiceMock.getTransferringStatus(any(String.class), any(String.class)))
				.thenReturn(Transaction.Status.PROCESSING);

		this.verifySuccess(this.doGET(statusTransferURL)).andExpect(
				jsonPath("$..transferStatus").exists());
	}

	@Test
	public void checkStatusFail() throws Exception {
		when(p2pTransferServiceMock.getTransferringStatus(any(String.class),any(String.class)))
				.thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(statusTransferURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value(unknownMessage))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

	@Test
	public void getTransferDetailSuccess() throws Exception {
		// mock data
		P2PTransferDraft transferDraft = new P2PTransferDraft();
		transferDraft.setAccessTokenID(fakeAccessToken);
		transferDraft.setAmount(new BigDecimal(100.00));
		transferDraft.setFullname("Apinya Ukachoke");
		transferDraft.setID("1111");
		transferDraft.setMobileNumber("0899999999");
		transferDraft.setOtpReferenceCode("qwer");
		transferDraft.setStatus(P2PTransferDraft.Status.OTP_CONFIRMED);
		transferDraft.setMessage("Hello world");
		
		P2PTransferTransaction transaction = new P2PTransferTransaction(transferDraft);
		transaction.setStatus(Transaction.Status.SUCCESS);

		P2PTransactionConfirmationInfo confirmationInfo = new P2PTransactionConfirmationInfo();
		confirmationInfo.setTransactionDate("01/01/13");
		confirmationInfo.setTransactionID("1111111111");

		transaction.setConfirmationInfo(confirmationInfo);
		//set requestbody for doGET
		when(p2pTransferServiceMock.getTransactionResult(
				any(String.class),
				any(String.class))
			).thenReturn(transaction);

		when(profileServiceMock.getEwalletBalance(any(String.class)))
			.thenReturn(new BigDecimal(100.00));


		this.verifySuccess(this.doGET(getTransferDetailURL))
				.andExpect(jsonPath("$..mobileNumber").value("089-999-9999"))
				.andExpect(jsonPath("$..amount").exists())
				.andExpect(jsonPath("$..recipientName").exists())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists())
				.andExpect(jsonPath("$..currentBalance").exists())
				.andExpect(jsonPath("$..personalMessage").exists());
	}

	@Test
	public void getTransferDetailFail() throws Exception {

		when(p2pTransferServiceMock.getTransactionResult(
				any(String.class),
				any(String.class))
			).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getTransferDetailURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value(unknownMessage))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
	}

}
