package th.co.truemoney.product.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class GenericBean {
	@JsonProperty(value = "signup.verify.email.url")
	private String signupVerifyEmailUrl;

	@JsonProperty(value = "signup.verify.mobile.url")
	private String signupVerifyMobileUrl;

	@JsonProperty(value = "signup.url")
	private String signupUrl;

	@JsonProperty(value = "statusCode.notEnoughMoney")
	private String statusCode;

	@JsonProperty(value = "addmoney.cashcard.url")
	private String topupCashcard;

	@JsonProperty(value = "topup.truemove.url")
	private String topupTruemoveHcard;

	@JsonProperty(value = "topup.truemove.otp.url")
	private String topUpTruemoveOTP;

	@JsonProperty(value = "signin.url")
	private String signinUserEmail;

	@JsonProperty(value = "billpay.verify.url")
	private String verifyBillPay;

	@JsonProperty(value = "billpay.confirm.url")
	private String confirmBillPay;

	@JsonProperty(value = "billpay.billinfo.url")
	private String billInformationByBarcode;

	@JsonProperty(value = "instruction.addmoney.direct.debit")
	private String addMoneyDirectInstruction;

	@JsonProperty(value = "instruction.addmoney.atm")
	private String addMoneyATMInstruction;

	@JsonProperty(value = "instruction.addmoney.kiosk")
	private String addMoneyKioskInstruction;

	@JsonProperty(value = "instruction.addmoney.tmx")
	private String addMoneyTMX;

	@JsonProperty(value = "instruction.addmoney.trm")
	private String addMoneyTRM;

	@JsonProperty(value = "addmoney.ewallet.directdebit.information.url")
	private String addMoneyEwalletDirectDebit;

	@JsonProperty(value = "login.mock")
	private String loginMock;

	@JsonProperty(value = "addmoney.verify.ewallet.mock")
	private String addMoneyVerifyMock;

	@JsonProperty(value = "addmoney.otp.ewallet.mock")
	private String addMoneySendOTPMock;

	@JsonProperty(value = "addmoney.confirm.ewallet.mock")
	private String addMoneyConfirmMock;

	@JsonProperty(value = "addmoney.status.ewallet.mock")
	private String addMoneyCheckStatusMock;
	
	@JsonProperty(value = "addmoney.ewallet.directdebit.mock")
	private String addMoneyDirectdebitInfoMock;

	public String getSignupVerifyEmailUrl() {
		return signupVerifyEmailUrl;
	}

	public void setSignupVerifyEmailUrl(String signupVerifyEmailUrl) {
		this.signupVerifyEmailUrl = signupVerifyEmailUrl;
	}

	public String getSignupVerifyMobileUrl() {
		return signupVerifyMobileUrl;
	}

	public void setSignupVerifyMobileUrl(String signupVerifyMobileUrl) {
		this.signupVerifyMobileUrl = signupVerifyMobileUrl;
	}

	public String getSignupUrl() {
		return signupUrl;
	}

	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getTopupCashcard() {
		return topupCashcard;
	}

	public String getTopupTruemoveHcard() {
		return topupTruemoveHcard;
	}

	public void setTopupTruemoveHcard(String topupTruemoveHcard) {
		this.topupTruemoveHcard = topupTruemoveHcard;
	}

	public String getTopUpTruemoveOTP() {
		return topUpTruemoveOTP;
	}

	public void setTopUpTruemoveOTP(String topUpTruemoveOTP) {
		this.topUpTruemoveOTP = topUpTruemoveOTP;
	}

	public void setTopupCashcard(String topupCashcard) {
		this.topupCashcard = topupCashcard;
	}

	public String getSigninUserEmail() {
		return signinUserEmail;
	}

	public void setSigninUserEmail(String signinUserEmail) {
		this.signinUserEmail = signinUserEmail;
	}

	public String getVerifyBillPay() {
		return verifyBillPay;
	}

	public void setVerifyBillPay(String verifyBillPay) {
		this.verifyBillPay = verifyBillPay;
	}

	public String getConfirmBillPay() {
		return confirmBillPay;
	}

	public void setConfirmBillPay(String confirmBillPay) {
		this.confirmBillPay = confirmBillPay;
	}

	public String getBillInformationByBarcode() {
		return billInformationByBarcode;
	}

	public void setBillInformationByBarcode(String billInformationByBarcode) {
		this.billInformationByBarcode = billInformationByBarcode;
	}

	public String getAddMoneyDirectInstruction() {
		return addMoneyDirectInstruction;
	}

	public void setAddMoneyDirectInstruction(String addMoneyDirectInstruction) {
		this.addMoneyDirectInstruction = addMoneyDirectInstruction;
	}

	public String getAddMoneyATMInstruction() {
		return addMoneyATMInstruction;
	}

	public void setAddMoneyATMInstruction(String addMoneyATMInstruction) {
		this.addMoneyATMInstruction = addMoneyATMInstruction;
	}

	public String getAddMoneyKioskInstruction() {
		return addMoneyKioskInstruction;
	}

	public void setAddMoneyKioskInstruction(String addMoneyKioskInstruction) {
		this.addMoneyKioskInstruction = addMoneyKioskInstruction;
	}

	public String getAddMoneyTMX() {
		return addMoneyTMX;
	}

	public void setAddMoneyTMX(String addMoneyTMX) {
		this.addMoneyTMX = addMoneyTMX;
	}

	public String getAddMoneyTRM() {
		return addMoneyTRM;
	}

	public void setAddMoneyTRM(String addMoneyTRM) {
		this.addMoneyTRM = addMoneyTRM;
	}

	public String getAddMoneyEwalletDirectDebit() {
		return addMoneyEwalletDirectDebit;
	}

	public void setAddMoneyEwalletDirectDebit(String addMoneyEwalletDirectDebit) {
		this.addMoneyEwalletDirectDebit = addMoneyEwalletDirectDebit;
	}

	public String getLoginMock() {
		return loginMock;
	}

	public void setLoginMock(String loginMock) {
		this.loginMock = loginMock;
	}

	public String getAddMoneyVerifyMock() {
		return addMoneyVerifyMock;
	}

	public void setAddMoneyVerifyMock(String addMoneyVerifyMock) {
		this.addMoneyVerifyMock = addMoneyVerifyMock;
	}

	public String getAddMoneySendOTPMock() {
		return addMoneySendOTPMock;
	}

	public void setAddMoneySendOTPMock(String addMoneySendOTPMock) {
		this.addMoneySendOTPMock = addMoneySendOTPMock;
	}

	public String getAddMoneyConfirmMock() {
		return addMoneyConfirmMock;
	}

	public void setAddMoneyConfirmMock(String addMoneyConfirmMock) {
		this.addMoneyConfirmMock = addMoneyConfirmMock;
	}

	public String getAddMoneyCheckStatusMock() {
		return addMoneyCheckStatusMock;
	}

	public void setAddMoneyCheckStatusMock(String addMoneyCheckStatusMock) {
		this.addMoneyCheckStatusMock = addMoneyCheckStatusMock;
	}

	public String getAddMoneyDirectdebitInfoMock() {
		return addMoneyDirectdebitInfoMock;
	}

	public void setAddMoneyDirectdebitInfoMock(String addMoneyDirectdebitInfoMock) {
		this.addMoneyDirectdebitInfoMock = addMoneyDirectdebitInfoMock;
	}
	
}
