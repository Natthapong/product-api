package th.co.truemoney.product.api.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestActivityType {
	
	@Test
	public void getActivityType() {
		assertEquals(WalletActivity.TYPE.TOPUP_MOBILE, WalletActivity.getType("topup_mobile"));
		assertEquals(WalletActivity.TYPE.ADD_MONEY, WalletActivity.getType("add_money"));
		assertEquals(WalletActivity.TYPE.TRANSFER, WalletActivity.getType("transfer"));
		assertEquals(WalletActivity.TYPE.BILLPAY, WalletActivity.getType("billpay"));
		assertEquals(WalletActivity.TYPE.BONUS, WalletActivity.getType("bonus"));
	}
}
