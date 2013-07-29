package th.co.truemoney.product.api.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OTPBean {
	
	@NotNull
	@Size(min=10, max=10)
	private String mobileNumber;
	@NotNull
	@Size(min=4, max=4)
	private String otpRefCode;
	@NotNull
	@Size(min=6, max=6)
	private String otpString;
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getOtpRefCode() {
		return otpRefCode;
	}
	public void setOtpRefCode(String otpRefCode) {
		this.otpRefCode = otpRefCode;
	}
	public String getOtpString() {
		return otpString;
	}
	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}
	public OTP toOTPObj() {
		return new OTP(this.mobileNumber.trim(), this.otpRefCode.trim(), this.otpString.trim());
	}
}
