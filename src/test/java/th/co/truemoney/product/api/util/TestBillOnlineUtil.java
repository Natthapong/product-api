package th.co.truemoney.product.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

public class TestBillOnlineUtil {
	
	private BillReferenceUtil billReferenceUtil;
	
	@Before
	public void setup(){
		billReferenceUtil = new BillReferenceUtil();
	}
	
	@Test
	public void sucessGetBillOnlineProperty(){
		assertEquals(false, billReferenceUtil.isOnlineInquiry("mea"));
	}
	
	@Test
	public void failGetBillOnlineProperty(){
		assertFalse(billReferenceUtil.isOnlineInquiry("xxx"));
	}
	
}
