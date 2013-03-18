package th.co.truemoney.product.api.domain;

public class TopupOrderConfirmRequest {
	
	private String topUpOrderId;
	
	private String otpString;

	public String getTopUpOrderId() {
		return topUpOrderId;
	}

	public void setTopUpOrderId(String topUpOrderId) {
		this.topUpOrderId = topUpOrderId;
	}

	public String getOtpString() {
		return otpString;
	}

	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}

}
