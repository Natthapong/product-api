package th.co.truemoney.product.api.domain;

public class TopupOrderConfirmRequest {

	private String quoteID;

	private String otpString;

	private String otpRefCode;
	
	private String mobileNumber;

	private String checksum;

	public String getQuoteID() {
		return quoteID;
	}

	public void setQuoteID(String quoteID) {
		this.quoteID = quoteID;
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
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
