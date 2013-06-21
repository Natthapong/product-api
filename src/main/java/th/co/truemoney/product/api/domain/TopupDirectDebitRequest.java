package th.co.truemoney.product.api.domain;

import java.math.BigDecimal;

public class TopupDirectDebitRequest {

	private String sourceOfFundID;

	private BigDecimal amount;

	private String checksum;

	public String getSourceOfFundID() {
		return sourceOfFundID;
	}

	public void setSourceOfFundID(String sourceOfFundID) {
		this.sourceOfFundID = sourceOfFundID;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
