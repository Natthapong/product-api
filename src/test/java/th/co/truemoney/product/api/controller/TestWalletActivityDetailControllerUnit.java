package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.domain.WalletActivity.TYPE;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public class TestWalletActivityDetailControllerUnit extends BaseTestController {
	
	@Autowired
	WalletActivityController controller;

	private static final String fakeAccessTokenID = "111111";

	@Test
	public void activityDetailsBBCL() throws Exception{

		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("bblc_c");
		detail.setRef1("************1234");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(3L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/bblc@2x.png", section1.get("logoURL"));
		assertEquals("บัตรเครดิตธนาคารกรุงเทพ", section1.get("titleTh"));
		assertEquals("bblc", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("หมายเลขบัตรเครดิต", cell1.get("titleTh"));
		assertEquals("Account Number", cell1.get("titleEn"));
		assertEquals("************1234", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
	}
}

