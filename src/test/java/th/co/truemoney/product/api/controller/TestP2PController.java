package th.co.truemoney.product.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestP2PController extends BaseTestController {

	String fakeAccessToken = "111111111111";
	String transferMoneyURL = String.format("/transfer/draft-transaction/%s",
			fakeAccessToken);
	String verifyTransferURL = String.format("/transfer/draft-transaction/%s/%s",
			"1111111111111", fakeAccessToken);

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
				p2pTransferServiceMock.createDraftTransaction(
						any(P2PDraftRequest.class), any(String.class)))
				.thenReturn(draftTransaction);

		this.verifySuccess(this.doPOST(transferMoneyURL, data));
	}

	@Test
	public void createDraftTransactionFail() throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mobileNumber", "0898888888");
		data.put("amount", "100.00");

		when(
				p2pTransferServiceMock.createDraftTransaction(
						any(P2PDraftRequest.class), any(String.class)))
				.thenThrow(new ServiceInventoryException("", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPOST(transferMoneyURL, data))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
		;
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
				p2pTransferServiceMock.sendOTP(any(String.class), any(String.class)))
				.thenReturn(otp);
		
		when(
				p2pTransferServiceMock.getDraftTransactionDetails(any(String.class), 
						any(String.class))).thenReturn(draftTransaction);
		
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
				p2pTransferServiceMock.sendOTP(any(String.class), any(String.class)))
				.thenThrow(new ServiceInventoryException("", "", "TMN-PRODUCT"));
		
		when(
				p2pTransferServiceMock.getDraftTransactionDetails(any(String.class), 
						any(String.class))).thenReturn(draftTransaction);

		this.verifyFailed(this.doPUT(verifyTransferURL))
				.andExpect(jsonPath("$.code").value(""))
				.andExpect(jsonPath("$.messageEn").value("Unknown Message"))
				.andExpect(jsonPath("$.namespace").value("TMN-PRODUCT"))
				.andExpect(jsonPath("$.messageTh").value(containsString("")));
		;
	}
	
}
