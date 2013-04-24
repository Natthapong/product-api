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
			"/topup/mobile/sendotp/%s/%s", "1111111111", fakeAccessToken);

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
		assertValidateAmountFormatFail("1001");
	}
	
	@Test
	public void failToVerifyTopUpWhenAmountNotDivideByTen() {
		assertValidateAmountFormatFail("501");
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
				"https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png",
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
		
		when(topUpMobileServiceMock.sendOTP(anyString(), anyString()))
				.thenReturn(otp);
		
		when(topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(), anyString()))
		.thenReturn(createTopUpMobileDraftStub());
		
		this.verifySuccess(this.doPOST(sendOTPURL))
			.andExpect(jsonPath("data").exists());

	}
	
	@Test
	public void sendOTPFail() throws Exception {

		when(topUpMobileServiceMock.sendOTP(anyString(), anyString()))
				.thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));
		
		when(topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(), anyString()))
		.thenReturn(createTopUpMobileDraftStub());

        this.verifyFailed(this.doPOST(sendOTPURL));

	}

	private TopUpMobileDraft createTopUpMobileDraftStub() {
		TopUpMobile topUpMobileInfo = new TopUpMobile();
		topUpMobileInfo.setAmount(new BigDecimal(500));
		topUpMobileInfo.setMobileNumber("0894445266");
		topUpMobileInfo
				.setLogo("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bank/scb@2x.png");
		topUpMobileInfo.setServiceFee(new ServiceFeeInfo("THB", new BigDecimal(
				15)));
		topUpMobileInfo.setSourceOfFundFees(createSOF());
		TopUpMobileDraft draft = new TopUpMobileDraft();
		draft.setAccessTokenID(fakeAccessToken);
		draft.setTopUpMobileInfo(topUpMobileInfo);
		draft.setID("1111111111");

		return draft;
	}

	private SourceOfFund[] createSOF() {
		SourceOfFund sourceOfFundFee = new SourceOfFund();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFeeRate(new BigDecimal("0"));
		sourceOfFundFee.setFeeRateType("THB");

		return new SourceOfFund[] { sourceOfFundFee };
	}

}
