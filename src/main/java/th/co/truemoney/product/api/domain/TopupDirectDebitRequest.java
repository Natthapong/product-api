package th.co.truemoney.product.api.domain;

import java.math.BigDecimal;

public class TopupDirectDebitRequest {
	
	private String sourceOfFundId;
	
	private BigDecimal amount;
	
	private String checksum;

	public String getSourceOfFundId() {
		return sourceOfFundId;
	}

	public void setSourceOfFundId(String sourceOfFundId) {
		this.sourceOfFundId = sourceOfFundId;
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
