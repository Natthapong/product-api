package th.co.truemoney.product.api.domain;

public class TopupOrderConfirmRequest {

	private String topupOrderID;

	private String otpString;

	private String otpRefCode;

	private String checksum;

	public String getTopupOrderID() {
		return topupOrderID;
	}

	public void setTopupOrderID(String topupOrderID) {
		this.topupOrderID = topupOrderID;
	}

	public String getOtpString() {
		return otpString;
	}

	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}

	public String getOtpRefCode() {
		return otpRefCode;
	}

	public void setOtpRefCode(String otpRefCode) {
		this.otpRefCode = otpRefCode;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
