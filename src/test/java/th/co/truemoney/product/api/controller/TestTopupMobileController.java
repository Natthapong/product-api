package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

public class TestTopupMobileController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";

	private static final String verifyTopUpMobileURL = String.format(
			"/topup/mobile/draft/verifyAndCreate/%s", fakeAccessToken);
	private static final String sendOTPURL = String.format(
			"/topup/mobile/sendotp/%s/%s", "1111111111", fakeAccessToken);
	private static final String confirmOTPURL = String.format(
			"/topup/mobile/confirm/%s/%s", "1111111111", fakeAccessToken);
	private static final String checkStatusURL = String.format(
			"/topup/mobile/%s/status/%s", "1111111111", fakeAccessToken);
	private static final String getTopupDetailsURL = String.format(
			"/topup/mobile/%s/details/%s", "1111111111", fakeAccessToken);

	@Autowired
	TopupMobileController controller;

	private Map<String, String> request = new HashMap<String, String>();
	
	private String accessToken = "accessToken";

	@Test
	public void failToVerifyTopUpWhenMobileIsIncorrectFormat() {
		assertValidateMobileFormatFail("xxxxxxxxxx");
		assertValidateMobileFormatFail("0893333");
		assertValidateMobileFormatFail("09 080 3242");
	}

	private void assertValidateMobileFormatFail(String mobileNumber) {
		try {
			request.put("mobileNumber", mobileNumber);
			request.put("amount", "100");
			controller.verifyAndCreate(accessToken, request);
			Assert.fail();
		} catch (InvalidParameterException ex) {
			Assert.assertEquals("40002", ex.getMessage());
		}
	}

	@Test
	public void failToVerifyTopUpWhenAmountLessThanMinimum() {
		assertValidateAmountFormatFail("9");
	}

	@Test
	public void failToVerifyTopUpWhenAmountLessThanMaximum() {
		assertValidateAmountFormatFail("1010");
	}

	@Test
	public void failToVerifyTopUpWhenAmountNotDivideByTen() {
		assertValidateAmountFormatFail("501");
	}

	@Test
	public void failToVerifyTopUpWhenAmountVeryBig() {
		assertValidateAmountFormatFail("50154654645645645645654654898989898989898989898989898978978089080");
	}

	private void assertValidateAmountFormatFail(String amount) {
		try {
			request.put("mobileNumber", "0899999999");
			request.put("amount", amount);
			controller.verifyAndCreate(accessToken, request);
			Assert.fail();
		} catch (InvalidParameterException ex) {
			Assert.assertEquals("60000", ex.getMessage());
		}
	}

	@Test
	public void verifyAndCreateSuccess() {

		when(
				topUpMobileServiceMock.verifyAndCreateTopUpMobileDraft(
						anyString(), any(BigDecimal.class), anyString()))
				.thenReturn(createTopUpMobileDraftStub());

		ProductResponse response = null;
		try {
			request.put("mobileNumber", "0894445266");
			request.put("amount", "500");
			response = controller.verifyAndCreate(accessToken, request);
		} catch (InvalidParameterException ex) {
			ex.printStackTrace();
			Assert.fail();
		}

		Map<String, Object> data = response.getData();
		Assert.assertEquals("1111111111", data.get("draftTransactionID"));
		Assert.assertEquals(
				"https://secure.truemoney-dev.com/m/images/logo_bank/scb@2x.png",
				data.get("logoURL"));
		Assert.assertEquals("089-444-5266", data.get("mobileNumber"));
		Assert.assertEquals(new BigDecimal(500), data.get("amount"));
		Assert.assertEquals(new BigDecimal(15), data.get("fee"));
		Assert.assertEquals(new BigDecimal(515), data.get("totalAmount"));

		verify(topUpMobileServiceMock).verifyAndCreateTopUpMobileDraft(
				anyString(), any(BigDecimal.class), anyString());
	}

	@Test
	public void integrationTestVerifyMobileNumber() throws Exception {

		when(
				topUpMobileServiceMock.verifyAndCreateTopUpMobileDraft(
						anyString(), any(BigDecimal.class), anyString()))
				.thenReturn(createTopUpMobileDraftStub());

		Map<String, String> reqBody = new HashMap<String, String>();
		reqBody.put("mobileNumber", "0894445266");
		reqBody.put("amount", "500");

		this.verifySuccess(this.doPOST(verifyTopUpMobileURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..mobileNumber").value("089-444-5266"));
	}

	@Test
	public void integrationTestSendOTPSuccess() throws Exception {

		OTP otp = new OTP();
		otp.setMobileNumber("0894445266");
		otp.setOtpString("123456");
		otp.setReferenceCode("qwer");

		when(transactionAuthenServiceMock.requestOTP(anyString(), anyString()))
				.thenReturn(otp);

		when(
				topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(),
						anyString())).thenReturn(createTopUpMobileDraftStub());

		this.verifySuccess(this.doPOST(sendOTPURL)).andExpect(
				jsonPath("data").exists());

	}

	@Test
	public void sendOTPFail() throws Exception {

		when(transactionAuthenServiceMock.requestOTP(anyString(), anyString()))
				.thenThrow(
						new ServiceInventoryException(400, "", "",
								"TMN-PRODUCT"));

		when(
				topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(),
						anyString())).thenReturn(createTopUpMobileDraftStub());

		this.verifyFailed(this.doPOST(sendOTPURL));

	}

	@Test
	public void integrationTestConfirmOTPSuccess() throws Exception {

		when(
				transactionAuthenServiceMock.verifyOTP(anyString(),
						any(OTP.class), anyString())).thenReturn(
				TopUpMobileDraft.Status.OTP_CONFIRMED);

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifySuccess(this.doPUT(confirmOTPURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..status").value("CONFIRMED"));

		Mockito.verify(topUpMobileServiceMock).performTopUpMobile(anyString(), anyString());
	}

	@Test
	public void confirmOTPFail() throws Exception {

		when(
				transactionAuthenServiceMock.verifyOTP(anyString(),
						any(OTP.class), anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifyFailed(this.doPUT(confirmOTPURL, reqBody));

		Mockito.verify(topUpMobileServiceMock, Mockito.never()).performTopUpMobile(anyString(), anyString());

	}

	@Test
	public void integrationTestCheckStatusSuccess() throws Exception {

		when(
				topUpMobileServiceMock.getTopUpMobileStatus(anyString(),
						anyString())).thenReturn(
				TopUpMobileTransaction.Status.SUCCESS);

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifySuccess(this.doGET(checkStatusURL, reqBody))
				.andExpect(jsonPath("data").exists())
				.andExpect(jsonPath("$..status").value("SUCCESS"));
	}

	@Test
	public void checkStatusFail() throws Exception {

		when(
				topUpMobileServiceMock.getTopUpMobileStatus(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("otpString", "123456");
		reqBody.put("otpRefCode", "QWE");

		this.verifyFailed(this.doGET(checkStatusURL, reqBody));
	}

	@Test
	public void integrationTestGetTopupDetailsSuccess() throws Exception {

		when(
				topUpMobileServiceMock.getTopUpMobileResult(anyString(),
						anyString())).thenReturn(
				createTopUpMobileTransactionStub());

		when(profileServiceMock.getEwalletBalance(anyString())).thenReturn(
				new BigDecimal(5000));

		this.verifySuccess(this.doGET(getTopupDetailsURL)).andExpect(
				jsonPath("data").exists());
	}

	@Test
	public void getTopupDetailsFail() throws Exception {

		when(
				topUpMobileServiceMock.getTopUpMobileResult(anyString(),
						anyString())).thenThrow(
				new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getTopupDetailsURL));
	}

	private TopUpMobileDraft createTopUpMobileDraftStub() {
		TopUpMobile topUpMobileInfo = new TopUpMobile();
		topUpMobileInfo.setAmount(new BigDecimal(500));
		topUpMobileInfo.setMobileNumber("0894445266");
		topUpMobileInfo
				.setLogo("https://secure.truemoney-dev.com/m/images/logo_bank/scb@2x.png");
		topUpMobileInfo.setServiceFee(new ServiceFeeInfo("THB", new BigDecimal(
				15)));
		topUpMobileInfo.setSourceOfFundFees(createSOF());
		TopUpMobileDraft draft = new TopUpMobileDraft();
		draft.setAccessTokenID(fakeAccessToken);
		draft.setTopUpMobileInfo(topUpMobileInfo);
		draft.setID("1111111111");

		return draft;
	}

	private TopUpMobileTransaction createTopUpMobileTransactionStub() {
		TopUpMobileTransaction transaction = new TopUpMobileTransaction();
		transaction.setType("type");
		transaction.setStatus(Status.SUCCESS);
		transaction.setID("1111111111");

		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft();
		topUpMobileDraft.setAccessTokenID(fakeAccessToken);
		topUpMobileDraft.setID("1111111111");
		topUpMobileDraft.setStatus
		(th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status.OTP_CONFIRMED);
		topUpMobileDraft.setType("type");
		topUpMobileDraft.setOtpReferenceCode("QWE");
		topUpMobileDraft.setSelectedSourceOfFundType("EW");
		topUpMobileDraft.setTransactionID("1111111111");

		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setAmount(new BigDecimal(500));
		topUpMobile.setID("1111111111");
		topUpMobile.setLogo("https://secure.truemoney-dev.com/m/images/logo_bank/scb@2x.png");
		topUpMobile.setMaxAmount(new BigDecimal(1000));
		topUpMobile.setMinAmount(new BigDecimal(10));
		topUpMobile.setMobileNumber("0894445266");
		topUpMobile.setRemainBalance(new BigDecimal(1000));

		ServiceFeeInfo serviceFee = new ServiceFeeInfo();
		serviceFee.setFeeRate(new BigDecimal(10));
		serviceFee.setFeeRateType("THB");

		topUpMobile.setServiceFee(serviceFee);

		SourceOfFund[] sof = new SourceOfFund[1];
		sof[0]= new SourceOfFund();
		sof[0].setFeeRate(new BigDecimal(0));
		sof[0].setFeeRateType("THB");
		sof[0].setSourceType("EW");

		topUpMobile.setSourceOfFundFees(sof);

		topUpMobileDraft.setTopUpMobileInfo(topUpMobile);

		transaction.setDraftTransaction(topUpMobileDraft);

		TopUpMobileConfirmationInfo confirmationInfo = new TopUpMobileConfirmationInfo();
		confirmationInfo.setTransactionDate("25/04/13 10:03");
		confirmationInfo.setTransactionID("1111111111");

		transaction.setConfirmationInfo(confirmationInfo);

		return transaction;
	}

	private SourceOfFund[] createSOF() {
		SourceOfFund sourceOfFundFee = new SourceOfFund();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFeeRate(new BigDecimal("0"));
		sourceOfFundFee.setFeeRateType("THB");

		return new SourceOfFund[] { sourceOfFundFee };
	}

}
