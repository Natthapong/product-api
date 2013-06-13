package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public class TestWalletActivityDetailControllerUnit extends BaseTestController {
	
	@Autowired
	WalletActivityController controller;

	String accessTokenID = "1111111111";

	private String getLogoURL(String billCode) {
		return "http://localhost:8080/m/tmn_webview/images/logo_bill/" + billCode + "@2x.png";
	}
	
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm"); 
	
	private Date getDefaltTxnDate() {
		try {
			return df.parse("2013/02/10 15:35");
		} catch (ParseException e) {
			// do nothing
		} 
		return null;
	}
		
	@SuppressWarnings("unchecked")
	private void executeTest(ExpectedBillPayResult expected, ActivityDetail detail) {
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertTrue(data.containsKey("section1"));
		assertTrue(data.containsKey("section2"));
		assertTrue(data.containsKey("section3"));
		assertTrue(data.containsKey("section4"));
		
		Map<String, String> sec1 = (Map<String, String>) data.get("section1");
		Map<String, Object> sec2 = (Map<String, Object>) data.get("section2");
		Map<String, Object> sec3 = (Map<String, Object>) data.get("section3");
		Map<String, Object> sec4 = (Map<String, Object>) data.get("section4");
		
		// check section 1
		assertEquals(expected.getLogoURL(), sec1.get("logoURL"));
		assertEquals(expected.getTitleTh(), sec1.get("titleTh"));
		assertEquals(expected.getTitleEn(), sec1.get("titleEn"));

		// check section 2
		assertTrue(sec2.containsKey("column1"));
		assertFalse(sec2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) sec2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertEquals(expected.getRef1TitleTh(), cell1.get("titleTh"));
		assertEquals(expected.getRef1TitleEn(), cell1.get("titleEn"));
		assertEquals(expected.getRef1Value(), cell1.get("value"));
		
		if (expected.getRef2Value() == null) {
			assertFalse(column21.containsKey("cell2"));
		} else {
			Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
			assertEquals(expected.getRef2TitleTh(), cell2.get("titleTh"));
			assertEquals(expected.getRef2TitleEn(), cell2.get("titleEn"));
			assertEquals(expected.getRef2Value(), cell2.get("value"));
		}
		
		// check section 3
		assertTrue(sec3.containsKey("column1"));
		assertTrue(sec3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) sec3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) sec3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));
		
		// check section 4
		assertTrue(sec4.containsKey("column1"));
		assertTrue(sec4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) sec4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) sec4.get("column2");
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

	private ActivityDetail buildBillPayActivity(
			String action, String ref1Value, String ref2Value) {
		
		ActivityDetail detail = new ActivityDetail();
		detail.setType("billpay");
		detail.setAction(action + "_c");
		detail.setRef1(ref1Value);
		detail.setRef2(ref2Value);
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionID("1234567890");
		detail.setTransactionDate(getDefaltTxnDate());
		return detail;
	}

	private ExpectedBillPayResult buildBillPayExpectedDetails(
			String action, String titleEn, String titleTh, 
			String ref1TitleEn, String ref1TitleTh, String ref1Value,
			String ref2TitleEn, String ref2TitleTh, String ref2Value) {
		
		ExpectedBillPayResult expected = new ExpectedBillPayResult();
		expected.setLogoURL(getLogoURL(action));
		expected.setTitleEn(titleEn);
		expected.setTitleTh(titleTh);
		expected.setRef1TitleEn(ref1TitleEn);
		expected.setRef1TitleTh(ref1TitleTh);
		expected.setRef1Value(ref1Value);
		expected.setRef2TitleEn(ref2TitleEn);
		expected.setRef2TitleTh(ref2TitleTh);
		expected.setRef2Value(ref2Value);
		return expected;
	}
	
	private void test(String action, String titleEn, String titleTh, 
			String ref1TitleEn, String ref1TitleTh, String ref1Value, 
			String ref2TitleEn, String ref2TitleTh, String ref2Value) {
		
		ActivityDetail detail = buildBillPayActivity(action, ref1Value, ref2Value);
		
		ExpectedBillPayResult expected = buildBillPayExpectedDetails(action, titleEn, titleTh, 
																	 ref1TitleEn, ref1TitleTh, ref1Value, 
																	 ref2TitleEn, ref2TitleTh, ref2Value);
		executeTest(expected, detail);
	}
	
	@Test
	public void activityDetailsBBCL() throws Exception {
		String action = "bblc";
		String titleEn = action;
		String titleTh = "บัตรเครดิตธนาคารกรุงเทพ";
		String ref1Value = "************1234";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "หมายเลขบัตรเครดิต";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsTISCO() throws Exception {
		String action = "tisco";
		String titleEn = action;
		String titleTh = "ธนาคารทิสโก้";
		String ref1Value = "12345";
		String ref1TitleEn = "Ref. 1";
		String ref1TitleTh = "เลขที่อ้างอิง";
		String ref2Value = "54321";
		String ref2TitleEn = "Ref. 2";
		String ref2TitleTh = "สัญญาเลขที่";
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}

	@Test
	public void activityDetailsGHB() throws Exception {
		String action = "ghb";
		String titleEn = action;
		String titleTh = "ธนาคารอาคารสงเคราะห์";
		String ref1Value = "12345";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "เลขที่บัญชีเงินกู้";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsKTC() throws Exception {
		String action = "ktc";
		String titleEn = action;
		String titleTh = "บัตรเครดิต KTC";
		String ref1Value = "************1234";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "หมายเลขบัตรเครดิต";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsAEON() throws Exception {
		String action = "aeon";
		String titleEn = action;
		String titleTh = "บัตรอิออน";
		String ref1Value = "12345";
		String ref1TitleEn = "Ref. 1";
		String ref1TitleTh = "เลขที่อ้างอิง 1";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}

	@Test
	public void activityDetailsFC() throws Exception {
		String action = "fc";
		String titleEn = action;
		String titleTh = "กรุงศรีเฟิร์สชอยส์";
		String ref1Value = "************1234";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "หมายเลขบัตรเครดิต";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsKCC() throws Exception {
		String action = "kcc";
		String titleEn = action;
		String titleTh = "บัตรเครดิตกรุงศรี";
		String ref1Value = "************1234";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "หมายเลขบัตรเครดิต";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsPB() throws Exception {
		String action = "pb";
		String titleEn = action;
		String titleTh = "บัตรเพาเวอร์บาย";
		String ref1Value = "************1234";
		String ref1TitleEn = "Account Number";
		String ref1TitleTh = "หมายเลขบัตรเครดิต";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsUOB() throws Exception {
		String action = "uob";
		String titleEn = action;
		String titleTh = "บัตรเครดิตธนาคารยูโอบี";
		String ref1Value = "************1234";
		String ref1TitleEn = "Card Number";
		String ref1TitleTh = "หมายเลขบัตร";
		String ref2Value = null;
		String ref2TitleEn = null;
		String ref2TitleTh = null;
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsMISTINE() throws Exception {
		String action = "mistine";
		String titleEn = action;
		String titleTh = "มิสทีน";
		String ref1Value = "12345";
		String ref1TitleEn = "Customer Number";
		String ref1TitleTh = "รหัสสาวจำหน่าย";
		String ref2Value = "54321";
		String ref2TitleEn = "Bill Number";
		String ref2TitleTh = "เลขที่ใบส่งของ";
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
	
	@Test
	public void activityDetailsBWC() throws Exception {
		String action = "bwc";
		String titleEn = action;
		String titleTh = "เอ็ม พาวเวอร์";
		String ref1Value = "12345";
		String ref1TitleEn = "Customer Number";
		String ref1TitleTh = "รหัสสาวจำหน่าย";
		String ref2Value = "54321";
		String ref2TitleEn = "Bill Number";
		String ref2TitleTh = "เลขที่ใบส่งของ";
		
		test(action, titleEn, titleTh, 
				ref1TitleEn, ref1TitleTh, ref1Value, 
				ref2TitleEn, ref2TitleTh, ref2Value);
	}
}

class ExpectedBillPayResult {
	String logoURL;
	String titleEn;
	String titleTh;
	String ref1TitleEn;
	String ref1TitleTh;
	String ref1Value;
	String ref2TitleEn;
	String ref2TitleTh;
	String ref2Value;
	
	public String getLogoURL() {
		return logoURL;
	}
	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}
	public String getTitleEn() {
		return titleEn;
	}
	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
	public String getTitleTh() {
		return titleTh;
	}
	public void setTitleTh(String titleTh) {
		this.titleTh = titleTh;
	}
	public String getRef1TitleEn() {
		return ref1TitleEn;
	}
	public void setRef1TitleEn(String ref1TitleEn) {
		this.ref1TitleEn = ref1TitleEn;
	}
	public String getRef1TitleTh() {
		return ref1TitleTh;
	}
	public void setRef1TitleTh(String ref1TitleTh) {
		this.ref1TitleTh = ref1TitleTh;
	}
	public String getRef1Value() {
		return ref1Value;
	}
	public void setRef1Value(String ref1Value) {
		this.ref1Value = ref1Value;
	}
	public String getRef2TitleEn() {
		return ref2TitleEn;
	}
	public void setRef2TitleEn(String ref2TitleEn) {
		this.ref2TitleEn = ref2TitleEn;
	}
	public String getRef2TitleTh() {
		return ref2TitleTh;
	}
	public void setRef2TitleTh(String ref2TitleTh) {
		this.ref2TitleTh = ref2TitleTh;
	}
	public void setRef2Value(String ref2Value) {
		this.ref2Value = ref2Value;
	}
	public String getRef2Value() {
		return ref2Value;
	}
}

