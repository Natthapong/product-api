package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.domain.WalletActivity.TYPE;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public class TestWalletActivityDetailControllerUnit extends BaseTestController {
	
	@Autowired
	WalletActivityController controller;

	private final String accessTokenID = "111111";
	
	private final String LOGO_URL = "http://localhost:8080/m/tmn_webview/images/logo_bill/";

	ActivityDetailBuilder builder;
	
	@Before
	public void setup() {
		super.setup();
		builder = ActivityDetailBuilder.defaultBillPayTestValues();
	}

	@Test
	public void activityDetailsBBCL() throws Exception{
		String originalBillCode = "bblc_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();

		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode + "@2x.png");
		expected.put("titleTh", "บัตรเครดิตธนาคารกรุงเทพ");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);

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
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsTISCO() throws Exception{
		String originalBillCode = "tisco_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = builder.setAction(originalBillCode).setRef1("12345").setRef2("54321").build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();

		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode + "@2x.png");
		expected.put("titleTh", "ธนาคารทิสโก้");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("เลขที่อ้างอิง", cell1.get("titleTh"));
		assertEquals("Ref. 1", cell1.get("titleEn"));
		assertEquals("12345", cell1.get("value"));
		
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertEquals("สัญญาเลขที่", cell2.get("titleTh"));
		assertEquals("Ref.2", cell2.get("titleEn"));
		assertEquals("54321", cell2.get("value"));
		
		checkSection3(data);
		checkSection4(data);
	}

	@Test
	public void activityDetailsGHB() throws Exception{
		String originalBillCode = "ghb_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = builder.setAction(originalBillCode).setRef1("12345").build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode + "@2x.png");
		expected.put("titleTh", "ธนาคารอาคารสงเคราะห์");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		assertEquals("เลขที่บัญชีเงินกู้", cell1.get("titleTh"));
		assertEquals("Account Number", cell1.get("titleEn"));
		assertEquals("12345", cell1.get("value"));

		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsKTC() throws Exception{
		String originalBillCode = "ktc_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "บัตรเครดิต KTC");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsAEON() throws Exception{
		String originalBillCode = "aeon_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = builder.setAction(originalBillCode).setRef1("12345").build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "บัตรอิออน");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		assertEquals("เลขที่อ้างอิง 1", cell1.get("titleTh"));
		assertEquals("Ref. 1", cell1.get("titleEn"));
		assertEquals("12345", cell1.get("value"));
		
		checkSection3(data);
		checkSection4(data);
	}

	@Test
	public void activityDetailsFC() throws Exception{
		String originalBillCode = "fc_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "กรุงศรีเฟิร์สชอยส์");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsKCC() throws Exception{
		String originalBillCode = "kcc_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "บัตรเครดิตกรุงศรีอยุธยา");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsPB() throws Exception{
		String originalBillCode = "pb_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "บัตรเพาเวอร์บาย");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsUOB() throws Exception{
		String originalBillCode = "uob_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = ActivityDetailBuilder.defaultCreditCardBillPayment(originalBillCode).build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();
		
		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode +"@2x.png");
		expected.put("titleTh", "ธนาคาร UOB");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
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
		assertEquals("หมายเลขบัตร", cell1.get("titleTh"));
		assertEquals("Card Number", cell1.get("titleEn"));
		assertEquals("************1234", cell1.get("value"));
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsMISTINE() throws Exception{
		String originalBillCode = "mistine_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = builder.setAction(originalBillCode).setRef1("12345").setRef2("54321").build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();

		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode + "@2x.png");
		expected.put("titleTh", "มิสทีน");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสสาวจำหน่าย", cell1.get("titleTh"));
		assertEquals("Customer Number", cell1.get("titleEn"));
		assertEquals("12345", cell1.get("value"));
		
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertEquals("เลขที่ใบส่งของ", cell2.get("titleTh"));
		assertEquals("Bill Number", cell2.get("titleEn"));
		assertEquals("54321", cell2.get("value"));
		
		checkSection3(data);
		checkSection4(data);
	}
	
	@Test
	public void activityDetailsBWC() throws Exception{
		String originalBillCode = "bwc_c";
		String billCode = Utils.removeSuffix(originalBillCode);
		
		ActivityDetail detail = builder.setAction(originalBillCode).setRef1("12345").setRef2("54321").build();
		
		when(this.activityServiceMock.getActivityDetail(3L, accessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), accessTokenID);
		Map<String, Object> data = resp.getData();

		Map<String, String> expected = new HashMap<String, String>();
		expected.put("logoURL", LOGO_URL + billCode + "@2x.png");
		expected.put("titleTh", "MPower");
		expected.put("titleEn", billCode);
		checkSection1(expected, data);
		
		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสสาวจำหน่าย", cell1.get("titleTh"));
		assertEquals("Customer Number", cell1.get("titleEn"));
		assertEquals("12345", cell1.get("value"));
		
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertEquals("เลขที่ใบส่งของ", cell2.get("titleTh"));
		assertEquals("Bill Number", cell2.get("titleEn"));
		assertEquals("54321", cell2.get("value"));
		
		checkSection3(data);
		checkSection4(data);
	}
	
	private void checkSection1(Map<String, String> expected, Map<String, Object> data) {
		assertTrue(data.containsKey("section1"));
		Map<String, String> actual = (Map<String, String>) data.get("section1");
		assertEquals(expected.get("logoURL"), actual.get("logoURL"));
		assertEquals(expected.get("titleTh"), actual.get("titleTh"));
		assertEquals(expected.get("titleEn"), actual.get("titleEn"));
	}

	private void checkSection3(Map<String, Object> data) {
		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>)data.get("section3");
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
	}
	

	private void checkSection4(Map<String, Object> data) {
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

class ActivityDetailBuilder {
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	private ActivityDetail activityDetail;
	
	public ActivityDetailBuilder() {
		activityDetail = new ActivityDetail();
	}
	
	public static ActivityDetailBuilder defaultValues() {
		return new ActivityDetailBuilder();
	}
	
	public static ActivityDetailBuilder defaultBillPayTestValues() {
		ActivityDetailBuilder builder = defaultValues();
		builder.setType(TYPE.BILLPAY.name());
		builder.setAction("tmvh_c");
		builder.setRef1("0897116568");
		builder.setAmount(new BigDecimal(23455.50));
		builder.setServiceFee(new BigDecimal(1234.50));
		builder.setTransactionID("1234567890");
		try {
			builder.setTransactionDate(df.parse("2013/02/10 15:35"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return builder;
	}
	
	public static ActivityDetailBuilder defaultCreditCardBillPayment(String action) {
		String maskedCreditCardNumber = "************1234";
		return defaultBillPayTestValues().setAction(action).setRef1(maskedCreditCardNumber);
	}
	
	public ActivityDetailBuilder setType(String type) {
		activityDetail.setType(type);
		return this;
	}
	
	public ActivityDetailBuilder setAction(String action) {
		activityDetail.setAction(action);
		return this;
	}
	
	public ActivityDetailBuilder setRef1(String ref1) {
		activityDetail.setRef1(ref1);
		return this;
	}
	
	public ActivityDetailBuilder setRef2(String ref2) {
		activityDetail.setRef2(ref2);
		return this;
	}
	
	public ActivityDetailBuilder setAmount(BigDecimal amount) {
		activityDetail.setAmount(amount);
		return this;
	}
	
	public ActivityDetailBuilder setServiceFee(BigDecimal serviceFee) {
		activityDetail.setServiceFee(serviceFee);
		return this;
	}
	
	public ActivityDetailBuilder setTransactionDate(Date transactionDate) {
		activityDetail.setTransactionDate(transactionDate);
		return this;
	}
	
	public ActivityDetailBuilder setTransactionID(String transactionID) {
		activityDetail.setTransactionID(transactionID);
		return this;
	}
	
	public ActivityDetail build() {
		return activityDetail;
	}

}
