package th.co.truemoney.product.api.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class TestUtils {
	
	@Test
	public void testFormatAmount() {
		assertEquals("12,345.50", Utils.formatAmount(new BigDecimal(12345.50)));
		assertEquals("0.00", Utils.formatAmount(new BigDecimal(0)));
	}
	
	@Test
	public void testFormatMobileNumber() {
		assertEquals("089-123-4567", Utils.formatMobileNumber("0891234567"));
	}
	
	@Test
	public void testFormatTelNumber() {
		assertEquals("02-123-4567", Utils.formatTelNumber("021234567"));
	}
}
