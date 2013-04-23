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
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

public class TestTopupMobileController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";

	private static final String verifyTopUpMobileURL = String.format(
			"/topup/mobile/draft/verifyAndCreate/%s", fakeAccessToken);
	private static final String sendOTPURL = String.format(
			"/topup/mobile/sendotp/%s", fakeAccessToken);

	@Autowired
	TopupMobileController controller;

	private Map<String, String> request;
	private String accessToken = "accessToken";

	@Before
	public void setUp() throws Exception {
		request = new HashMap<String, String>();
	}

	@Test
	public void failToVerifyTopUpWhenMobileIsIncorrectFormat() {
		assertValidationFail("xxxxxxxxxx");
		assertValidationFail("0893333");
		assertValidationFail("09 080 3242");
	}

	@Test
	public void verifySuccess() {

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
				"https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png",
				data.get("logoURL"));
		Assert.assertEquals("0894445266", data.get("mobileNumber"));
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
				.andExpect(jsonPath("data").exists());
	}

	@Test
	public void integrationTestSendOTPSuccess() throws Exception {

		when(topUpMobileServiceMock.sendOTP(anyString(), anyString()))
				.thenReturn(OTPStub());

		Map<String, String> reqBody = new HashMap<String, String>();
		reqBody.put("draftTransactionID", "1111111111");

		this.verifySuccess(this.doPOST(sendOTPURL, reqBody)).andExpect(
				jsonPath("data").exists());

	}
	
	@Test
	public void sendOTPFail() throws Exception {

		when(topUpMobileServiceMock.sendOTP(anyString(), anyString()))
				.thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));
		
		Map<String, String> reqBody = new HashMap<String, String>();
		reqBody.put("draftTransactionID", "1111111111");

        this.verifyFailed(this.doPOST(sendOTPURL, reqBody));

	}

	private void assertValidationFail(String mobileNumber) {
		try {
			request.put("mobileNumber", mobileNumber);
			controller.verifyAndCreate(accessToken, request);
			Assert.fail();
		} catch (InvalidParameterException ex) {
		}
	}

	private TopUpMobileDraft createTopUpMobileDraftStub() {
		TopUpMobile topUpMobileInfo = new TopUpMobile();
		topUpMobileInfo.setAmount(new BigDecimal(500));
		topUpMobileInfo.setMobileNumber("0894445266");
		topUpMobileInfo
				.setLogo("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png");
		topUpMobileInfo.setServiceFee(new ServiceFeeInfo("THB", new BigDecimal(
				15)));
		topUpMobileInfo.setSourceOfFundFees(createBillPaySOF());
		TopUpMobileDraft draft = new TopUpMobileDraft();
		draft.setAccessTokenID(fakeAccessToken);
		draft.setTopUpMobileInfo(topUpMobileInfo);
		draft.setID("1111111111");

		return draft;
	}

	private OTP OTPStub() {
		OTP otp = new OTP();
		otp.setMobileNumber("0894445266");
		otp.setOtpString("123456");
		otp.setReferenceCode("qwer");
		return otp;
	}

	private SourceOfFund[] createBillPaySOF() {
		SourceOfFund sourceOfFundFee = new SourceOfFund();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFeeRate(new BigDecimal("0"));
		sourceOfFundFee.setFeeRateType("THB");

		return new SourceOfFund[] { sourceOfFundFee };
	}

}
