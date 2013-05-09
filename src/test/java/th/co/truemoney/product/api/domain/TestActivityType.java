package th.co.truemoney.product.api.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestActivityType {
	
	@Test
	public void getActivityType() {
		assertEquals(WalletActivity.TYPE.TOPUP_MOBILE, WalletActivity.getEnum("topup_mobile"));
		assertEquals(WalletActivity.TYPE.ADD_MONEY, WalletActivity.getEnum("add_money"));
		assertEquals(WalletActivity.TYPE.TRANSFER, WalletActivity.getEnum("transfer"));
		assertEquals(WalletActivity.TYPE.BILLPAY, WalletActivity.getEnum("billpay"));
		assertEquals(WalletActivity.TYPE.BONUS, WalletActivity.getEnum("bonus"));
	}
}
