package th.co.truemoney.product.api.domain;

import java.math.BigDecimal;

public class TopupDirectDebitRequest {
	
	private String sourceOfFundId;
	
	private BigDecimal amount;

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

}
